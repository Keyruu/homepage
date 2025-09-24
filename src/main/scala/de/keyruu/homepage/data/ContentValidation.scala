package de.keyruu.homepage.data.blog

import java.time.LocalDate
import zio.prelude.*
import scala.util.Try

case class ValidationException(errors: List[String])
    extends Exception(errors.mkString(", "))

object ContentValidation:
  def nonEmpty(field: String)(value: String): Validation[String, String] =
    if value.trim.nonEmpty then Validation.succeed(value)
    else Validation.fail(s"$field cannot be empty")

  def lengthBetween(field: String, min: Int, max: Int)(
      value: String
  ): Validation[String, String] =
    val length = value.length
    if length >= min && length <= max then Validation.succeed(value)
    else
      Validation.fail(
        s"$field must be between $min and $max characters (got $length)"
      )

  def minLength(field: String, min: Int)(
      value: String
  ): Validation[String, String] =
    if value.length >= min then Validation.succeed(value)
    else Validation.fail(s"$field must be at least $min characters")

  def maxLength(field: String, max: Int)(
      value: String
  ): Validation[String, String] =
    if value.length <= max then Validation.succeed(value)
    else Validation.fail(s"$field must be at most $max characters")

  def parseDate(field: String)(value: String): Validation[String, LocalDate] =
    Validation
      .fromTry(Try(LocalDate.parse(value)))
      .mapError(_ =>
        s"Invalid date format for $field: $value (expected YYYY-MM-DD)"
      )

  def parseBoolean(field: String)(value: String): Validation[String, Boolean] =
    value.toLowerCase match
      case "true"  => Validation.succeed(true)
      case "false" => Validation.succeed(false)
      case ""      => Validation.succeed(false)
      case _ => Validation.fail(s"Invalid boolean value for $field: $value")

  def slug(value: String): Validation[String, String] =
    if value.isEmpty then Validation.fail("slug cannot be empty")
    else if !value.matches("^[a-z0-9]+(?:-[a-z0-9]+)*$") then
      Validation.fail(s"Invalid slug format: $value")
    else Validation.succeed(value)

  def email(value: String): Validation[String, String] =
    if value.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$") then
      Validation.succeed(value)
    else Validation.fail(s"Invalid email format: $value")

  def url(value: String): Validation[String, String] =
    if value.matches("^https?://.*") then Validation.succeed(value)
    else Validation.fail(s"Invalid URL format: $value")

  extension (validationA: Validation[String, String])
    def andThen(
        validationB: String => Validation[String, String]
    ): Validation[String, String] =
      validationA.flatMap(validationB)
