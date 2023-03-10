package com.example.androidhitratecalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
            ContentView()
        }
    }
}

//region ContentView
@Composable fun ContentView() {
    val agilityAmount = remember {  mutableStateOf("") }
    val evadeLuckAmount = remember {  mutableStateOf("") }
    val evadeBonusAmount = remember {  mutableStateOf("") }
    val dexAmount = remember {  mutableStateOf("") }
    val accLuckAmount = remember {  mutableStateOf("") }
    val accBonusAmount = remember {  mutableStateOf("") }

    AndroidHitRateCalcTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column() {
                Column() {
                    Row(modifier = Modifier.padding(20.dp)) {
                        Text(text = "Evasion", modifier = Modifier.padding(horizontal = 20.dp))
                        Text(text = "${HitRateCalculator.getEvasionRate(agility = agilityAmount.value, luck = evadeLuckAmount.value, bonus = evadeBonusAmount.value)}%", modifier = Modifier.padding(horizontal = 80.dp))
                    }

                    TraitRow(name = "Agility", amount = agilityAmount)
                    TraitRow(name = "Luck", amount = evadeLuckAmount)
                    TraitRow(name = "Bonus", amount = evadeBonusAmount)
                }

                Column() {
                    Row(modifier = Modifier.padding(20.dp)) {
                        Text(text = "Accuracy", modifier = Modifier.padding(horizontal = 20.dp))
                        Text(text = "${HitRateCalculator.getAccuracyRate(dex = dexAmount.value, luck = accLuckAmount.value, bonus = accBonusAmount.value)}%", modifier = Modifier.padding(horizontal = 80.dp))
                    }
                    TraitRow(name = "Dexterity", amount = dexAmount)
                    TraitRow(name = "Luck", amount = accLuckAmount)
                    TraitRow(name = "Bonus", amount = accBonusAmount)
                }

                Row() {
                    Text(text = "Chance to Evade", modifier = Modifier.padding(horizontal = 20.dp))
                    Text(text = "${HitRateCalculator.getChanceToEvade(evasionRate = HitRateCalculator.getEvasionRate(agility = agilityAmount.value, luck = evadeLuckAmount.value, bonus = evadeBonusAmount.value), accuracyRate = HitRateCalculator.getAccuracyRate(dex = dexAmount.value, luck = accLuckAmount.value, bonus = accBonusAmount.value))}%")
                }
            }
        }
    }
}
//endregion
class TraitsViewModel : ViewModel() {
    val agilityAmount = MutableLiveData("")
    val luckAmount = MutableLiveData("")
    val bonusAmount = MutableLiveData("")
}


@Composable fun TraitRow(name: String, amount: MutableState<String>) {
    val focusManager = LocalFocusManager.current

    Row {
        Surface(color = Color.Black) {
            Text(
                text = name,
                color = Color.White,
                modifier = Modifier.padding(17.dp)
            )
        }
        OutlinedTextField(
            value = amount.value,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            onValueChange = { amount.value = it }
        )
    }
}


//region TraitsSection
//endregion

//region TraitList
@Composable fun TraitList(traitList: List<Trait>) {
    Column() {
        traitList.forEach { trait -> 
//            TraitRow(trait = trait)
        }
    }
}
//endregion

//region TraitRow
//@Composable fun TraitRow(name: String, amount: String) {
//    Row {
//        Surface(color = Color.Black) {
//            Text(
//                text = name,
//                color = Color.White,
//                modifier = Modifier.padding(17.dp)
//            )
//        }
//        OutlinedTextField(
//            value = amount,
//            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
//            onValueChange = { amount = it }
//        )
//    }
//}

//endregion

// region Preview
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ContentView()
}
//endregion

//region HitRateDataModel
class HitRateDataModel: ViewModel() {
    val evasionTraits = mutableStateOf(listOf(Trait(name = "Agility"), Trait(id = 1, name = "Luck"), Trait(id = 2, name = "Bonus")))
    val accuracyTraits = mutableStateOf(listOf(Trait(name = "Dexterity"), Trait(id = 1, name = "Luck"), Trait(id = 2, name = "Bonus")))
}
//endregion

//region Trait
data class Trait(var id: Int = 0, var name: String = "", var amount: String = "")
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
