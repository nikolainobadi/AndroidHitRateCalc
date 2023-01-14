package com.example.androidhitratecalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidhitratecalc.ui.theme.AndroidHitRateCalcTheme
import java.lang.Math.pow
import java.lang.StrictMath.pow
import java.time.format.TextStyle
import java.util.Observable
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

        }
    }
}

@Composable
fun ContentView() {
    var dataModel = remember {  HitRateDataModel() }

    AndroidHitRateCalcTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {

        }
    }
}



class HitRateDataModel {
    val agility = mutableStateOf("")
    val evadeLuck = mutableStateOf("")
    val evadeBonus = mutableStateOf("")
    val dex = mutableStateOf("")
    val accLuck = mutableStateOf("")
    val accbonus = mutableStateOf("")

}

// region Preview
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ContentView()
}
//endregion

//region HitRateCalculator
class HitRateCalculator {
    companion object {
        fun getEvasionRate(agility: String, luck: String, bonus: String): Int {
            val luckValue = getLuckValue(luck)
            val agilityValue = getAgilityValue(agility)
            val bonusValue = getBonusValue(bonus)
            val baseRate = getBaseRate(luck = luckValue, otherTrait = agilityValue)

            return makeRoundedInt(baseRate + bonusValue)
        }

        fun getAccuracyRate(dex: String, luck: String, bonus: String): Int {
            val luckValue = getLuckValue(luck)
            val dexValue = getDexterityValue(dex)
            val bonusValue = getBonusValue(bonus)
            val baseRate = getBaseRate(luck = luckValue, otherTrait = dexValue)

            return makeRoundedInt(baseRate + bonusValue)
        }

        fun getHitRate(accuracyRate: Int, evasionRate: Int): Int {
            return makeRoundedInt(accuracyRate.toDouble() - evasionRate.toDouble())
        }

        fun getChanceToEvade(evasionRate: Int, accuracyRate: Int): Int {
            return 100 - getHitRate(accuracyRate, evasionRate)
        }
        private fun makeRoundedInt(num: Double): Int { return kotlin.math.round(num).toInt() }
        private fun getBonusValue(other: String): Double { return other.toDoubleOrNull() ?: 0.0 }
        private fun getBaseRate(luck: Double, otherTrait: Double): Double { return (luck + otherTrait) * 100 }
        private fun getLuckValue(luck: String): Double {
            val number = luck.toDoubleOrNull()
            return if (number != null) {
                number.pow(0.96) / 200 - 1
            } else {
                0.0
            }
        }

        private fun getDexterityValue(dex: String): Double {
            val dex = dex.toDoubleOrNull()
            return if (dex != null) {
                (11 * dex.pow(0.2)) / 20
            } else {
                0.0
            }
        }

        private fun getAgilityValue(agility: String): Double {
            val agility = agility.toDoubleOrNull()
            return if (agility != null) {
                (11 * agility.pow(0.9)) / 1000
            } else {
                0.0
            }
        }
    }
}
//endregion
