package ru.thetax.views.main.header

import io.github.sanyavertolet.jswrappers.cookie.cookie
import io.github.sanyavertolet.jswrappers.cookie.getLanguageCode
import io.github.sanyavertolet.jswrappers.reacti18next.useTranslation
import js.objects.jso
import react.*
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h6
import react.dom.html.ReactHTML.img
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.option
import react.dom.html.ReactHTML.select
import ru.thetax.views.main.HeaderAndInputProps
import ru.thetax.views.main.parseAndCalculateYearSalary
import ru.thetax.views.utils.PeriodEnum
import ru.thetax.views.utils.externals.fontawesome.*
import ru.thetax.views.utils.PlatformLanguages
import ru.thetax.views.utils.changeLanguage
import web.cssom.*

/**
 * Header with an input of salary and meta information
 */
val headerAndInput = FC<HeaderAndInputProps> { props ->
    val (salaryInput, setSalaryInput) = useState("")
    val (t, i18n) = useTranslation("header")
    val (language, setSelectedLanguage) = useState(
        PlatformLanguages.getByCodeOrDefault(cookie.getLanguageCode())
    )
    useEffect(language) { i18n.changeLanguage(language) }

    div {
        // if one day you would like to make sticky fixed header - use:
        // row sticky-top
        className = ClassName("full-width-container")
        id = "gradient"
        div {
            className = ClassName("row justify-content-center pt-2")
            div {
                className = ClassName("col-xl-5 col-lg-5 col-md-7 col-sm-8 col-12 px-0")
                div {
                    className = ClassName("row")
                    div {
                        className = ClassName("col-5")
                        div {
                            className = ClassName("row d-flex align-items-center text-white")
                            img {
                                className = ClassName("ps-0")
                                src = "/img/logo.png"
                                style = jso {
                                    width = 2.5.rem
                                }
                            }
                            +"thetax.ru".t()
                        }
                    }
                    div {
                        className = ClassName("col-2 d-flex justify-content-center")
                        PlatformLanguages.entries.forEach { platformLanguage ->

                            div {
                                className = ClassName(if (platformLanguage != language) "logo" else "")
                                img {
                                    className = ClassName("me-2")
                                    src = "/img/flags/${platformLanguage.code}.svg"
                                    style = jso {
                                        if (platformLanguage == language) {
                                            opacity = 1.unsafeCast<Opacity>()
                                        } else {
                                            cursor = "pointer".unsafeCast<Cursor>()
                                            opacity = 0.7.unsafeCast<Opacity>()
                                        }
                                        width = 1.4.rem
                                    }
                                    onClick = {
                                        setSelectedLanguage(platformLanguage)
                                    }
                                }
                            }
                        }
                    }
                    div {
                        className = ClassName("col-5 d-flex justify-content-end")
                        a {
                            href = "https://github.com/orchestr7/thetax.ru"
                            fontAwesomeIcon(faGithub, "text-white me-3 fa-xl")
                        }
                        a {
                            href =
                                "https://www.vedomosti.ru/economics/news/2024/07/10/1049144-sovet-federatsii-odobril?from=read_also=2"
                            fontAwesomeIcon(faQuestionCircle, "text-white fa-xl")
                        }
                    }
                }

                div {
                    className = ClassName("row text-center mt-5")
                    h1 {
                        style = jso {
                            color = "rgb(253, 223, 197)".unsafeCast<Color>()
                        }
                        +"Российский налоговый калькулятор".t()
                    }

                    h6 {
                        className = ClassName("text-white")
                        +"Каким будет Ваш налог с 2025го года".t()
                    }

                    menu {
                        this.selectedMenu = props.selectedMenu
                        this.setSelectedMenu = props.setSelectedMenu
                    }
                }

                div {
                    className = ClassName("row mt-3 justify-content-center")
                    div {
                        className = ClassName("row justify-content-center mb-2")
                        // ====== input salary ====
                        div {
                            className = ClassName("col-7 ps-0 pe-1")
                            div {
                                className = ClassName("input-group-lg shadow mb-1")
                                input {
                                    className = ClassName("form-control custom-input ${props.validInput}")
                                    placeholder = "Доход до налога".t()
                                    style = jso {
                                        borderTopRightRadius = 0.unsafeCast<BorderTopRightRadius>()
                                        borderBottomRightRadius = 0.unsafeCast<BorderBottomRightRadius>()
                                    }
                                    title = "Зарплата в рублях".t()
                                    asDynamic()["data-toggle"] = "tooltip"
                                    asDynamic()["data-placement"] = "top"
                                    onChange = {
                                        val inputValue = it.target.value
                                        setSalaryInput(inputValue)
                                        val yearSalary = parseAndCalculateYearSalary(inputValue, props.periodInput)
                                        props.setSalaryDoubleInternal(yearSalary)
                                        // this "startTransition" logic prevents the following error:
                                        // A component suspended while responding to synchronous input.
                                        // This will cause the UI to be replaced with a loading indicator.
                                        // To fix, updates that suspend should be wrapped with startTransition.
                                        //
                                        // And it is somehow related to the check that we have in a parent class (where we check isValid)
                                        startTransition {
                                            if (yearSalary.isNaN()) props.setValidInput("is-invalid") else props.setValidInput(
                                                "is-valid"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        // ====== input period ====
                        div {
                            className = ClassName("col-5 ps-0 pe-0")
                            select {
                                className = ClassName("form-select shadow form-select-lg")
                                defaultValue = PeriodEnum.YEAR
                                option {
                                    value = PeriodEnum.YEAR
                                    +"В год".t()
                                }
                                style = jso {
                                    borderTopLeftRadius = 0.unsafeCast<BorderTopRightRadius>()
                                    borderBottomLeftRadius = 0.unsafeCast<BorderBottomRightRadius>()
                                }
                                option {
                                    value = PeriodEnum.MONTH
                                    +"В месяц".t()
                                }
                                onChange = {
                                    val period = PeriodEnum.valueOf(it.target.value)
                                    props.setPeriodInput(period)
                                    props.setSalaryDoubleInternal(parseAndCalculateYearSalary(salaryInput, period))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


