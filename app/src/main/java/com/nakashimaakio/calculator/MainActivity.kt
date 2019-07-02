package com.nakashimaakio.calculator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.math.absoluteValue

class MainActivity : AppCompatActivity() {

    private val stateInt = 0
    private val stateDec = 1
    private val stateRestart = 2
    private val stateEqual = 3
    private val que = Stack<String>()
    private var state = stateInt
    private var digit = 1
    private var maxDigit = 8

    override fun onCreate(savedInstancestate: Bundle?) {
        super.onCreate(savedInstancestate)
        setContentView(R.layout.activity_main)
    }


    //数字を入力
    fun onNum(view: View){
        if(state == stateRestart || state == stateEqual){
            numberView.text = (view as Button).text
            state = stateInt
            digit++
        } else if(numberView.text.toString() == "0"){
            numberView.text = (view as Button).text
        } else if(numberView.text.toString() == "-0"){
            numberView.text = "-" + (view as Button).text
        } else if (digit < maxDigit) {
            numberView.append((view as Button).text)
            digit++
        }
    }


    //四則演算記号(+,-,×,÷)を入力
    fun onCalc(view: View){
        Log.d("que_string","Calc Size:"+que.size)
        if(que.size >0) Log.d("que_string","Calc peek:"+que.peek())
        signView.text = (view as Button).text

        if(que.size == 0) {
            //queに何もない場合(=を入力した直後も含まれる)
            que.push(numberView.text.toString())
        } else if (state == stateInt || state == stateDec) {
            //直前に数字が入力された場合
            val tmpNum = queCalculation()
            if(tmpNum != "") que.push(tmpNum)
        } else {
            //直前に四則演算が入力された場合
            que.pop()
        }

        que.push((view as Button).text.toString())
        state = stateRestart
        digit = 0


    }

    //=を入力
    fun onEqual(view: View){
        Log.d("que_string","Equal Size:"+que.size)
        if(que.size >0) Log.d("que_string","Equal peek:"+que.peek())
        signView.text=""
        digit=0

        //直前に四則演算ボタンを押した場合
        if(state == stateRestart && que.size >1){
            que.pop()
            que.pop()
        }

        queCalculation()

        que.removeAllElements()
        state=stateEqual
    }

    //.を入力
    fun onDot(view: View){
        if(state==stateRestart ||state==stateEqual){
            numberView.text = "0."
            digit++
            state=stateDec
        } else if (digit < maxDigit && state == stateInt) {
            numberView.text = numberView.text.toString() + "."
            state = stateDec
        }
    }

    //Cを入力
    fun onC(view: View){
        signView.text=""
        numberView.text="0"
        state=stateInt
        digit=1
        que.removeAllElements()
    }

    //±を入力
    fun onSign(view: View){
        if(state==stateRestart){
            numberView.text="-0"
            state=stateInt
            digit++
        }
        else if(numberView.text.toString().substring(0,1)=="-") {
            numberView.text=numberView.text.substring(1)
        }
        else{
            numberView.text="-"+numberView.text
        }
    }


    //queの計算
    private fun queCalculation():String {
        var calcNum : Float
        if (que.size > 1) {
            val calc = que.pop()
            val num = que.pop().toFloat()
            calcNum = calculation(num, numberView.text.toString().toFloat(), calc)

            //表示できない数字の場合
            if (calcNum.absoluteValue > 99999999 || (calcNum.absoluteValue < 0.00000001 && calcNum.absoluteValue != 0.0.toFloat())) {
                numberView.text = String.format("%.3e", calcNum)
            }
        } else {
            calcNum = numberView.text.toString().toFloat()
        }
        //整数の場合
        if (calcNum == calcNum.toInt().toFloat()) numberView.text = String.format("%d", calcNum.toInt())
        //小数の場合
        else numberView.text = calcNum.toString()
        return calcNum.toString()

    }

    //実際の計算処理
    private fun calculation(num1:Float, num2:Float, calc: String):Float{
        if(calc=="+")return num1+num2
        else if(calc=="×")return num1*num2
        else if(calc=="÷")return num1/num2
        else return num1-num2
    }
}