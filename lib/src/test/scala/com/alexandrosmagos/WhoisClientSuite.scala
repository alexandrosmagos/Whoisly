package com.alexandrosmagos

import com.alexandrosmagos.parsers.{DomainParser, WhoisParser}
import org.junit.runner.RunWith
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.junit.JUnitRunner

import scala.concurrent.Await
import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class WhoisClientSuite extends AnyFunSuite {

  private val whoisTimeout = 1.minutes

  // Grouped: WHOIS Query functionality tests
  test("WHOIS query functionality and timing") {
    val whoisClient = new Whoisly()
    val domain      = "scala-lang.org"

    // Timing Async Query
    val asyncStartTime  = System.nanoTime()
    val futureResponse  = whoisClient.query(domain)
    val asyncResult     = Await.result(futureResponse, whoisTimeout)
    val asyncEndTime    = System.nanoTime()
    val asyncDurationMs = elapsedTimeInMillis(asyncStartTime, asyncEndTime)

    // Timing Sync Query
    val syncStartTime  = System.nanoTime()
    val syncResult     = whoisClient.querySync(domain, whoisTimeout)
    val syncEndTime    = System.nanoTime()
    val syncDurationMs = elapsedTimeInMillis(syncStartTime, syncEndTime)

    println(s"syncResult: $asyncResult")
    println(s"syncResult: $asyncResult")

    // Test assertions for Async Query
    assert(asyncDurationMs > 0, "Expected the async query to take some time")
    assert(asyncResult.response.createdDate.getOrElse("") == "2007-01-16T09:47:33Z", "Expected specific creation date")

    // Test assertions for Sync Query
    assert(syncDurationMs > 0, "Expected the sync query to take some time")
    assert(syncResult.response.expiryDate.getOrElse("") == "2027-01-16T09:47:33Z", "Expected specific expiry date")

    println(s"Async query time for $domain: $asyncDurationMs ms")
    println(s"Sync query time for $domain: $syncDurationMs ms")
  }

  // Grouped: DomainParser functionality tests
  test("DomainParser functionality and timing") {
    val domain = "scala-lang.org"

    // Timing parse
    val parseStartTime  = System.nanoTime()
    val (sld, tld)      = DomainParser.parse(domain)
    val parseEndTime    = System.nanoTime()
    val parseDurationMs = elapsedTimeInMillis(parseStartTime, parseEndTime)

    // Timing isValidDomain
    val isValidStartTime  = System.nanoTime()
    val isValid           = DomainParser.isValidDomain(domain)
    val isValidEndTime    = System.nanoTime()
    val isValidDurationMs = elapsedTimeInMillis(isValidStartTime, isValidEndTime)

    assert(sld.isDefined && tld.isDefined, "Expected the parse function to extract SLD and TLD")
    assert(isValid, "Expected the domain to be valid")

    println(f"DomainParser.parse execution time: $parseDurationMs%.2f ms for domain $domain")
    println(f"DomainParser.isValidDomain execution time: $isValidDurationMs%.2f ms for domain $domain")
  }

  // Grouped: WhoisParser functionality and timing
  test("WhoisParser functionality and timing with detailed raw WHOIS data") {
    val detailedRawWhoisData = """
                                 |Domain Name: EXAMPLE.COM
                                 |IDN Tag: example
                                 |Registrar IANA ID: 9999
                                 |Registrar: Example Registrar, Inc.
                                 |Created Date: 1999-12-31T23:59:59Z
                                 |Updated Date: 2023-01-01T12:00:00Z
                                 |Expiry Date: 2025-12-31T23:59:59Z
                                 |Registrant Name: John Doe
      """.stripMargin

    val startTime           = System.nanoTime()
    val (parsedData, error) = WhoisParser.parse(detailedRawWhoisData)
    val endTime             = System.nanoTime()

    val durationMs = elapsedTimeInMillis(startTime, endTime)

    assert(parsedData.domainName.contains("EXAMPLE.COM"), "Expected the parse function to extract domain name")
    assert(error.isEmpty, "Expected no error for a well-formed WHOIS response")

    println(f"WhoisParser.parse execution time with detailed data: $durationMs%.2f ms")
  }

  // Grouped: Additional WHOIS Query Tests
  test("Additional WHOIS query tests") {
    val whoisClient = new Whoisly()

    // Test for a non-existent domain
    val nonExistentFutureResponse = whoisClient.query("nonexistent1234567890domain.org")
    val nonExistentResult         = Await.result(nonExistentFutureResponse, whoisTimeout)
    assert(nonExistentResult.response.domainName.isEmpty, "Expected no domain name for a non-existent domain")
    assert(nonExistentResult.error.isDefined, "Expected an error message indicating no data")

    // Test for an invalid domain
    val invalidFutureResponse = whoisClient.query("invalid_domain")
    val invalidResult         = Await.result(invalidFutureResponse, whoisTimeout)
    assert(invalidResult.error.isDefined, "Expected an error for an invalid domain")
  }

  def elapsedTimeInMillis(
    startNanoTime: Long,
    endNanoTime: Long
  ): Double = (endNanoTime - startNanoTime) / 1e6 // nanoseconds to milliseconds

}
