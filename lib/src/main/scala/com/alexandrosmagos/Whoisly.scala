package com.alexandrosmagos

import com.alexandrosmagos.parsers.{DomainParser, WhoisParser}
import com.alexandrosmagos.utils.ServerUtils

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.net.{IDN, Socket}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, blocking}

class Whoisly {

  def query(
    domain: String
  ): Future[WhoisResponse] = Future {
    val asciiDomain = IDN.toASCII(domain)

    if (!DomainParser.isValidDomain(asciiDomain)) {
      WhoisResponse(asciiDomain, whoisData(), "", Some(s"Invalid domain: $asciiDomain"))
    } else {
      val server              = ServerUtils.determineWhoisServer(asciiDomain)
      val rawWhoisData        = performWhoisQuery(server, asciiDomain)
      val (parsedData, error) = WhoisParser.parse(rawWhoisData)

      WhoisResponse(asciiDomain, parsedData, rawWhoisData, error)
    }
  }

  private def performWhoisQuery(
    server: String,
    query: String
  ): String = blocking {
    try {
      val socket = new Socket(server, 43)
      try {
        val out = new PrintWriter(socket.getOutputStream, true)
        val in  = new BufferedReader(new InputStreamReader(socket.getInputStream))

        out.println(query)
        Iterator.continually(in.readLine()).takeWhile(_ != null).mkString("\n")
      } finally socket.close()
    } catch {
      case e: Exception => s"Error querying WHOIS data: ${e.getMessage}"
    }
  }

}
