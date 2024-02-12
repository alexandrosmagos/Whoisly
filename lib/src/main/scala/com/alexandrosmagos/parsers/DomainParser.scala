package com.alexandrosmagos.parsers

object DomainParser {

  def parse(
    domain: String
  ): (Option[String], Option[String]) = {
    val domainParts = domain.split("\\.").reverse
    domainParts.length match {
      case 0 | 1 => (None, None)
      case _     => (Some(domainParts(1)), Some(domainParts(0)))
    }
  }

  def isValidDomain(
    domain: String
  ): Boolean = {
    if (domain.isEmpty || domain.length > 253) return false

    val labels = domain.split("\\.")
    labels.forall(label => label.matches("^[a-zA-Z0-9-]{1,63}$") && !label.startsWith("-") && !label.endsWith("-"))
  }

  def getTLD(
    domain: String
  ): Option[String] =
    parse(domain)._2

  def getSLD(
    domain: String
  ): Option[String] =
    parse(domain)._1

}
