package ru.thetax.views.main

import io.github.sanyavertolet.jswrappers.i18next.TranslationFunctionWithReceiver
import io.github.sanyavertolet.jswrappers.reacti18next.useTranslation
import js.objects.jso
import react.ChildrenBuilder
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h5
import react.dom.html.ReactHTML.hr
import react.dom.html.ReactHTML.p
import ru.thetax.calculator.TaxCalculator
import ru.thetax.calculator.TaxDetail
import ru.thetax.calculator.TaxRates
import ru.thetax.views.utils.PeriodEnum
import ru.thetax.views.utils.formatNumber
import web.cssom.BorderRadius
import web.cssom.ClassName

external interface SalaryProps : Props {
    var salaryDoubleInternal: Double
    var periodInput: PeriodEnum
}

/**
 * Detailed tax calculations
 */
val cardWithCalculations = FC<SalaryProps> { props ->
    val (t) = useTranslation("calculator-card")

    val tax = TaxCalculator(props.salaryDoubleInternal, true, false)

    div {
        className = ClassName("col-xl-5 col-lg-5 col-md-7 col-sm-8 col-12")
        div {
            className = ClassName("card border-top-0")
            style = jso {
                borderRadius = 0.unsafeCast<BorderRadius>()
            }
            div {
                className = ClassName("card-header")
                when (props.periodInput) {
                    PeriodEnum.YEAR -> +"Расчет налога по среднему".t()
                    PeriodEnum.MONTH -> +"Расчет налога по среднему".t()
                    else -> TODO("Other periods are not supported yet")
                }
            }
            div {
                className = ClassName("card-body text-dark")
                generalRow("Доход до налога".t(), props.salaryDoubleInternal, t)
                hr {
                    className = ClassName("bg-danger border-2 border-top border-secondary")
                }
                rowWithRates(TaxRates.RATE_13, tax.taxDetails, t)
                rowWithRates(TaxRates.RATE_15, tax.taxDetails, t)
                rowWithRates(TaxRates.RATE_18, tax.taxDetails, t)
                rowWithRates(TaxRates.RATE_20, tax.taxDetails, t)
                rowWithRates(TaxRates.RATE_22, tax.taxDetails, t)
                hr {
                    className = ClassName("bg-danger border-2 border-top border-secondary")
                }
                generalRow("Общий налог".t(), tax.totalTax, t)
            }
        }
        div {
            className = ClassName("card border-top-0")
            style = jso {
                borderRadius = 0.unsafeCast<BorderRadius>()
            }
            div {
                className = ClassName("card-header")
                when (props.periodInput) {
                    PeriodEnum.YEAR -> +"Доход после налогов".t()
                    PeriodEnum.MONTH -> +"Доход после налогов".t()
                    else -> TODO("Other periods are not supported yet")
                }
            }
            div {
                className = ClassName("card-body text-dark")
                val salaryAfterTax = props.salaryDoubleInternal - tax.totalTax
                generalRow("В год".t(), salaryAfterTax, t)
                hr {
                    className = ClassName("bg-danger border-2 border-top border-secondary")
                }
                generalRow("В месяц".t(), salaryAfterTax / 12, t)
            }
        }
    }
}

fun parseAndCalculateYearSalary(inputSalary: String, periodInput: PeriodEnum): Double =
    try {
        val salary = inputSalary
            .replace(" ", "")
            .replace(",", ".")
            .toDouble()

        when (periodInput) {
            PeriodEnum.YEAR -> salary
            PeriodEnum.MONTH -> salary * 12
            PeriodEnum.WEEK -> salary * 52
            PeriodEnum.DAY -> salary * 245
        }
    } catch (e: NumberFormatException) {
        Double.NaN
    }

fun ChildrenBuilder.rowWithRates(rate: TaxRates, value: List<TaxDetail>, t: TranslationFunctionWithReceiver) {
    div {
        className = ClassName("row")
        div {
            className = ClassName("col-7")
            p {
                className = ClassName("ms-5 fs-6")
                +("Налог".t() + " ${rate.rate * 100}%")
            }
        }
        div {
            className = ClassName("col-5 fs-6 text-end")
            +((value.find { it.taxRate == rate }?.amount?.formatNumber() ?: "0.0") + " ₽")
        }
    }
}

fun ChildrenBuilder.generalRow(text: String, value: Double, t: TranslationFunctionWithReceiver) {
    div {
        className = ClassName("row")
        div {
            className = ClassName("col-7")
            h5 {
                +text.t()
            }
        }
        div {
            className = ClassName("col-5 text-end")
            +((if (value.isNaN() || value == 0.0) "0.0" else value.formatNumber()) + " ₽")
        }
    }
}
