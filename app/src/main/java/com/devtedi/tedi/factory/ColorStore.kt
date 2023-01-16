package com.devtedi.tedi.factory

import android.content.Context
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
/**
 *
    Abstract Class untuk mendefinisikan Requirement warna yang hendak digunakan
    maupun operasi - operasi untuk seluruh warna
    Karena propertinya banyak, jadi aku cuman kasih enssensialnya yak
    - Mat - > Matriks dalam open CV
    - Scalar -> Ya Scalar bang, konstanta
    - [color]Upper dan juga Lower -> Itu kayak representasi range warna tersebut
    - Mask , Masking dalam bit warna
 *
 * */
abstract class ColorStore(context : Context) {
    protected val mColorRed : String = "merah"
    protected val mColorBlue : String = "biru"
    protected val mColorGreen : String = "hijau"
    protected val mColorYellow : String = "kuning"
    protected val mColorPurple : String = "ungu"
    protected val mColorWhite : String = "putih"
    protected val mColorBlack : String = "hitam"
    protected val mColorGray : String = "abu"

    protected var inputImageMat : Mat = Mat()
    protected val mUniqueColor : MutableSet<String> = mutableSetOf()

    protected val redLower : Scalar = Scalar(0.0, 100.0, 100.0,);
    protected val redUpper  : Scalar = Scalar(5.0, 255.0, 255.0,);

    protected val kernel : Mat = Mat.ones(Size(5.0,5.0), CvType.CV_8UC1)
    // Green
    protected val greenLower : Scalar = Scalar(40.0, 100.0, 100.0)
    protected val greenUpper : Scalar = Scalar(70.0, 255.0, 255.0)

    protected val blueLower : Scalar = Scalar(90.0, 100.0, 100.0)
    protected val blueUpper : Scalar = Scalar(125.0, 255.0, 255.0)

    protected val orangeLower : Scalar = Scalar(15.0, 100.0, 100.0)
    protected val orangeUpper : Scalar = Scalar(20.0, 255.0, 255.0)

    protected val yellowLower : Scalar = Scalar(28.0, 100.0, 100.0)
    protected val yellowUpper : Scalar = Scalar(33.0, 255.0, 255.0)

    protected val purpleLower : Scalar = Scalar(135.0, 100.0, 100.0)
    protected val purpleUpper : Scalar = Scalar(150.0, 255.0, 255.0)

    protected val whiteLower : Scalar = Scalar(0.0, 0.0, 200.0)
    protected val whiteUpper : Scalar = Scalar(255.0, 255.0, 255.0)

    protected val blackLower : Scalar = Scalar(0.0, 0.0, 0.0)
    protected val blackUpper : Scalar = Scalar(0.0, 0.0, 30.0)

    protected val grayLower : Scalar = Scalar(0.0, 0.0, 30.0)
    protected val grayUpper : Scalar = Scalar(255.0,255.0,200.0)

    protected var hsvFrame : Mat = Mat()

    protected var redResult : Mat = Mat()
    protected var blueResult : Mat = Mat()
    protected var greenResult : Mat = Mat()
    protected var yellowResult : Mat = Mat()
    protected var orangeResult : Mat = Mat()
    protected var purpleResult : Mat = Mat()
    protected var whiteResult : Mat = Mat()
    protected var blackResult : Mat = Mat()
    protected var grayResult : Mat = Mat()

    protected  var redMask  : Mat = Mat()
    protected var blueMask : Mat = Mat()
    protected var greenMask : Mat = Mat()
    protected var yellowMask : Mat = Mat()
    protected var orangeMask : Mat = Mat()
    protected var purpleMask : Mat = Mat()
    protected var whiteMask : Mat = Mat()
    protected var blackMask : Mat = Mat()
    protected var grayMask : Mat = Mat()


    val arrOfColors = arrayListOf<String>(
        "merah", "biru", "hijau",
        "kuning", "oren", "ungu",
        "putih", "hitam", "abu"
    )

    val arrOfMaskColor = arrayListOf<Mat> (
        redMask, blueMask, greenMask,
        yellowMask, orangeMask, purpleMask,
        whiteMask, blackMask, grayMask
    )

    val arrOfResultColors = arrayListOf<Mat> (
        redResult, blueResult, greenResult,
        yellowResult, orangeResult, purpleResult,
        whiteResult, blackResult, grayResult
    )
    fun getUniqueColors() : Set<String>
    {
        return mUniqueColor
    }

    fun reset()
    {
        /*
            Reset, menghapus seluruh informasi yang ada
         */
        mUniqueColor.clear()
        redMask  = Mat()
        blueMask = Mat()
        greenMask = Mat()
        yellowMask = Mat()
        orangeMask = Mat()
        purpleMask = Mat()
        whiteMask = Mat()
        blackMask = Mat()
        grayMask = Mat()

        hsvFrame = Mat()
        inputImageMat = Mat()

    }

    protected open fun detectColorByBitwise()
    {
        /*
            https://stackoverflow.com/questions/42920810/in-opencv-is-mask-a-bitwise-and-operation
            Pake Bitwise deteks, hayooo ini bitwise operation
            Pada dasarnya fungsi yang ini cuman pengen tau apakah inputImageMat itu mengandung warna merah saja
            Kalau digame dev ini bisa disebut ibaratkan sebagai skill saja
            inputImageMat1&InputImageMat2 (x) itu adalah character dan redMask itu adalah representasi skill dari game
            kalau x dilakukan operasi ( AND ) dan ternyata tidak sama dengan redmask maka karakter tersebut tidak memiliki skill redmask, simple
         */
        Core.bitwise_and(inputImageMat, inputImageMat, redResult, redMask)
        Core.bitwise_and(inputImageMat, inputImageMat, greenResult, greenMask)
        Core.bitwise_and(inputImageMat, inputImageMat, blueResult, blueMask)
        Core.bitwise_and(inputImageMat, inputImageMat, orangeResult, orangeMask)
        Core.bitwise_and(inputImageMat, inputImageMat, yellowResult, yellowMask)
        Core.bitwise_and(inputImageMat, inputImageMat, purpleResult, purpleMask)
        Core.bitwise_and(inputImageMat, inputImageMat, whiteResult, whiteMask)
        Core.bitwise_and(inputImageMat, inputImageMat, blackResult, blackMask)
        Core.bitwise_and(inputImageMat, inputImageMat, grayResult, grayMask)
    }
    protected open fun iniateBoundaryColor ()
    {
        /*
         Membuat Boundary Color, lalu hasilnya disimpan di redMask
         Kek misalnya merah itu boundary dari lower berapa sampai upper berapa
         */
        Imgproc.cvtColor(inputImageMat, hsvFrame, Imgproc.COLOR_RGB2HSV)
        Core.inRange(hsvFrame, redLower, redUpper, redMask)
        Core.inRange(hsvFrame, greenLower, greenUpper, greenMask)
        Core.inRange(hsvFrame, blueLower, blueUpper, blueMask)
        Core.inRange(hsvFrame, orangeLower, orangeUpper, orangeMask)
        Core.inRange(hsvFrame, yellowLower, yellowUpper, yellowMask)
        Core.inRange(hsvFrame, purpleLower, purpleUpper, purpleMask)
        Core.inRange(hsvFrame, whiteLower, whiteUpper, whiteMask)
        Core.inRange(hsvFrame, blackLower, blackUpper, blackMask)
        Core.inRange(hsvFrame, grayLower, grayUpper, grayMask)
    }
    protected open fun dilateColor() {

        /*
            Dilate, https://docs.opencv.org/3.4/db/df6/tutorial_erosion_dilatation.html
         */
        Imgproc.dilate(redMask, redMask, kernel)
        Imgproc.dilate(greenMask, greenMask, kernel)
        Imgproc.dilate(blueMask, blueMask, kernel)
        Imgproc.dilate(orangeMask, orangeMask, kernel)
        Imgproc.dilate(yellowMask, yellowMask, kernel)
        Imgproc.dilate(purpleMask, purpleMask, kernel)
        Imgproc.dilate(whiteMask, whiteMask, kernel)
        Imgproc.dilate(blackMask, blackMask, kernel)
        Imgproc.dilate(grayMask, grayMask, kernel)
    }
    protected open fun doContour(maskColor : Mat, colorName : String )  {}

}