package com.alexandrosmagos

import com.alexandrosmagos.parsers.DomainParser
import com.alexandrosmagos.utils.ServerUtils
import org.junit.runner.RunWith
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.junit.JUnitRunner

import scala.concurrent.Await
import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class WhoisClientSuite extends AnyFunSuite {

  test("WHOIS domain server for .guru TLD is correct") {
    val whoisServer = ServerUtils.determineWhoisServer("sus.guru")

    assert(whoisServer == "whois.nic.guru")
  }

  test("WHOIS query for python.org contains specific data") {
    val whoisClient    = new Whoisly()
    val futureResponse = whoisClient.query("python.org")

    val result: WhoisResponse = Await.result(futureResponse, 10.seconds)

    assert(
      result.response.createdDate.getOrElse("") == "1995-03-27T05:00:00Z" &&
        result.response.expiryDate.getOrElse("") == "2033-03-28T05:00:00Z"
    )
  }

  test("Domain validation rejects invalid domain names") {
    val invalidDomains =
      Seq("", "invalid_domain", "-invalid.com", "invalid-.com", "in..valid.com", "toolong" * 63 + ".com")

    invalidDomains.foreach {
      domain =>
        val isValid = DomainParser.isValidDomain(domain)
        assert(!isValid, s"Domain $domain should be considered invalid")
    }
  }

  test("WHOIS query for an invalid domain returns error") {
    val whoisClient    = new Whoisly()
    val futureResponse = whoisClient.query("invalid_domain")

    val result = Await.result(futureResponse, 10.seconds)

    assert(result.error.isDefined, "Expected an error for an invalid domain")
    println(s"Error for invalid domain: ${result.error.get}")
  }

  test("WHOIS query for a non-existent domain returns no data") {
    val whoisClient    = new Whoisly()
    val futureResponse = whoisClient.query("nonexistent1234567890domain.org")

    val result = Await.result(futureResponse, 10.seconds)

    assert(result.response.domainName.isEmpty, "Expected no domain name for a non-existent domain")
    assert(result.error.isDefined, "Expected an error message indicating no data")
  }

  test("WHOIS query for a domain with special characters is handled correctly") {
    val whoisClient    = new Whoisly()
    val futureResponse = whoisClient.query("xn--dmin-moa0i.com") // Punycode representation for "dömäin.com"

    val result = Await.result(futureResponse, 10.seconds)

    assert(result.error.isEmpty, s"Expected no error for a domain with special characters, but got: ${result.error}")
    assert(result.response.domainName.isDefined, "Expected a domain name for a domain with special characters")
  }

  // Domain Parser tests
  test("parse valid domain") {
    val (sld, tld) = DomainParser.parse("alexandrosmagos.com")
    assert(sld.contains("alexandrosmagos"))
    assert(tld.contains("com"))
  }

  test("parse domain with subdomain") {
    val (sld, tld) = DomainParser.parse("sub.alexandrosmagos.com")
    assert(sld.contains("alexandrosmagos"))
    assert(tld.contains("com"))
  }

  test("isValidDomain with valid domain") {
    assert(DomainParser.isValidDomain("alexandrosmagos.com"))
  }

  test("isValidDomain with invalid domain") {
    assert(!DomainParser.isValidDomain("-alexandrosmagos.com")) // Starts with a hyphen
    assert(!DomainParser.isValidDomain("alexandrosmagos.com-")) // Ends with a hyphen
    assert(!DomainParser.isValidDomain("exa_mple.com"))         // Contains an underscore
    assert(!DomainParser.isValidDomain("alexandrosmagos..com")) // Contains consecutive dots
  }

}
