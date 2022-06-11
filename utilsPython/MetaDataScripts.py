#%%
from tflite_support import flatbuffers
from tflite_support import metadata
from tflite_support import metadata_schema_py_generated as metadata_fb

import os 
from absl import app
from absl import flags 
import tensorflow as tf

import flatbuffers
FLAGS = flags.FLAGS

def define_flags():
	flags.DEFINE_string("model_file", None, "Path to the model file.")
	flags.DEFINE_string("label_file_path", None, "Path to the label file.")
	flags.DEFINE_string("export_directory", None, "Path to save TFLiteModel with Metadata directory.")

	flags.mark_flag_as_required("model_file")
	flags.mark_flag_as_required("label_file_path")
	flags.mark_flag_as_required("export_directory")

class ModelSpecificInfo(object):

	def __init__(self, name, version, image_width,
		 		image_height, image_min, image_max, mean, std, num_classes, author):
		self.name = name 
		self.version = version
		self.image_width = image_width
		self.image_height = image_height
		self.image_min = image_min
		self.image_max = image_max
		self.mean = mean
		self.std = std
		self.num_classes = num_classes
		self.author = author 
	
MODEL_INFO = {
		"Object_class_rm_bg.tflite" : ModelSpecificInfo(
		name = "Object_class_rm_bg.tflite",
		version ="v1",
		image_width = 224,
		image_height = 224	,
		image_min = 0.0,
		image_max = 255.0,
		mean = [127.5],
		std = [127.5],
		num_classes = 7,
		author = "Thisable"
)
		
	}
class MetadataPopulatorForObjectDetection(object):
	"""
	Intinya masukin Metadata ke model tflite untuk dilakukan prediksi pada app nantinya
	"""
	def __init__(self, model_file, model_info, label_file_path):
		self.model_file = model_file
		self.model_info = model_info
		self.label_file_path = label_file_path
		self.metadata_buffer = None
	
	def populate(self):
		self.create_metadata()
		self.populateMetadata()

	def create_metadata(self):
		
		# Generate Model Information
		model_meta = metadata_fb.ModelMetadataT()
		model_meta.name = self.model_info.name
		model_meta.description = ("Hello World Model")
		model_meta.version = self.model_info.version
		model_meta.author = self.model_info.author
		print(model_meta.name)
		
		# Generate Input & Output Information

		input_meta = metadata_fb.TensorMetadataT()
		output_meta = metadata_fb.TensorMetadataT()


		#Generate Input metadata
		input_meta.name = "ObjectDetectionInputImage"
		input_meta.description = "Input image for Object Detection"
		input_meta.content = metadata_fb.ContentT()
		input_meta.content.contentProperties = metadata_fb.ImagePropertiesT()
		input_meta.content.contentProperties.colorSpace = (metadata_fb.ColorSpaceType.RGB)
		input_meta.content.contentPropertiesType = (metadata_fb.ContentProperties.ImageProperties)

		# Input Normalization 
		input_normalization = metadata_fb.ProcessUnitT()
		
		input_normalization.optionsType = metadata_fb.ProcessUnitOptions.NormalizationOptions
		input_normalization.options = metadata_fb.NormalizationOptionsT()
		input_normalization.options.mean =[127.5]
		input_normalization.options.std = [127.5]

		input_meta.processUnits = [input_normalization]
		# input stats
		input_stats = metadata_fb.StatsT()
		input_stats.min = [0.0]
		input_stats.max = [255.0]

		# Input Meta
		print(input_normalization)
		input_meta.stats = input_stats

		# Create Output metadata

		output_meta.name = "ObjectDetectionOutputImage"
		output_meta.description = "Output % probabilites and bbox for Object Detection Model"
		output_meta.content = metadata_fb.ContentT()
		output_meta.content.content_properties = metadata_fb.FeaturePropertiesT()
		output_meta.content.contentPropertiesType = (metadata_fb.ContentProperties.FeatureProperties)


		## Output stats
		output_stats = metadata_fb.StatsT()
		output_stats.min = [0.0]
		output_stats.max = [1.0]

		##assign the output_stats to the output_meta
		output_meta.stats = output_stats

		## LabelFile = 
		label_file = metadata_fb.AssociatedFileT()
		label_file.name = os.path.basename(self.label_file_path)
		label_file.description = "file location of the label file"
		label_file.type = metadata_fb.AssociatedFileType.TENSOR_AXIS_LABELS
		output_meta.associatedFiles = [label_file]


		# Create The Subgraph ( Pipeline )
		subgraph_meta = metadata_fb.SubGraphMetadataT()
		subgraph_meta.inputTensorMetadata = [input_meta]
		subgraph_meta.outputTensorMetadata = [output_meta]

		model_meta.subgraphMetadata = [subgraph_meta]


		b = flatbuffers.Builder(0)
		b.Finish(
			model_meta.Pack(b),
			metadata.MetadataPopulator.METADATA_FILE_IDENTIFIER
		)

		self.metadata_buffer = b.Output()

	def populateMetadata(self):
		"Populate Metadata and label file to the model file."

		populator = metadata.MetadataPopulator.with_model_file(self.model_file)
		populator.load_metadata_buffer(self.metadata_buffer)
		populator.load_associated_files([self.label_file_path])
		populator.populate()

def main(_) :
	model_file = FLAGS.model_file
	model_basename = os.path.basename(model_file)
	if model_basename not in MODEL_INFO :
		raise ValueError("Invalid model file name")
	
	export_model_path = os.path.join(FLAGS.export_directory, model_basename)
	print(model_file)
	print("Exporting model to {}".format(export_model_path))
	tf.io.gfile.copy(model_file, export_model_path, overwrite=False)

	## Generate Metadataobjects and put it into model
	populator = MetadataPopulatorForObjectDetection(
		export_model_path,
		MODEL_INFO[model_basename],
		FLAGS.label_file_path,
	)

	populator.populate()

	## Validate output model file by reading the metadata and product a json file

	displayer = metadata.MetadataDisplayer.with_model_file(export_model_path)
	export_json_file = os.path.join(FLAGS.export_directory, os.path.splitext(model_basename)[0] + ".json")
	
	json_file = displayer.get_metadata_json()
	with open(export_json_file, "w") as fuf:
		fuf.write(json_file)


	print("Finishied_populating metadata and associated file to the model")
	print(model_file)
	print("The metadata has been saved to : ")
	print(export_json_file)
#%%
if __name__ == "__main__" :
	define_flags()
	app.run(main)
# %%
