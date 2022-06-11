#%%
import tensorflow as tf
print(tf.__version__)
import tensorflow_hub as hub
# Convert the model.
model ="augment-hpc_fix.h5"
tflite_model = tf.keras.models.load_model(model, custom_objects={"KerasLayer": hub.KerasLayer})
print(tflite_model)
converter = tf.lite.TFLiteConverter.from_keras_model(tflite_model)
tflite_model = converter.convert()

# Save the model.
with open('final_SignLanguage_ack_hpc.tflite', 'wb') as f:
  f.write(tflite_model) 
# %%
