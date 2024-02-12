package com.alexandrosmagos.utils

import com.alexandrosmagos.parsers.DomainParser
import org.xbill.DNS._

import scala.collection.concurrent.TrieMap

object ServerUtils {

  private val whoisServerCache: TrieMap[String, String] = TrieMap(
    "com" -> "whois.verisign-grs.com",
    "net" -> "whois.verisign-grs.com",
    "org" -> "whois.pir.org",

    // ccTLDs
    "ai"      -> "whois.nic.ai",
    "au"      -> "whois.auda.org.au",
    "co"      -> "whois.nic.co",
    "ca"      -> "whois.cira.ca",
    "do"      -> "whois.nic.do",
    "eu"      -> "whois.eu",
    "gl"      -> "whois.nic.gl",
    "in"      -> "whois.registry.in",
    "io"      -> "whois.nic.io",
    "it"      -> "whois.nic.it",
    "me"      -> "whois.nic.me",
    "ro"      -> "whois.rotld.ro",
    "rs"      -> "whois.rnids.rs",
    "so"      -> "whois.nic.so",
    "us"      -> "whois.nic.us",
    "ws"      -> "whois.website.ws",
    "agency"  -> "whois.nic.agency",
    "app"     -> "whois.nic.google",
    "biz"     -> "whois.nic.biz",
    "country" -> "whois.uniregistry.net", // hardcoded because `whois.iana.org` sometimes returns "whois.uniregistry.net" or "whois.nic.country"
    "dev"     -> "whois.nic.google",
    "house"   -> "whois.nic.house",
    "health"  -> "whois.nic.health",
    "info"    -> "whois.nic.info",
    "link"    -> "whois.uniregistry.net",
    "live"    -> "whois.nic.live",
    "nyc"     -> "whois.nic.nyc",
    "one"     -> "whois.nic.one",
    "online"  -> "whois.nic.online",
    "shop"    -> "whois.nic.shop",
    "site"    -> "whois.nic.site",
    "xyz"     -> "whois.nic.xyz"
  )

  private val misspelledWhoisServerMap = Map(
    "www.gandi.net/whois"                            -> "whois.gandi.net",
    "who.godaddy.com/"                               -> "whois.godaddy.com",
    "whois.godaddy.com/"                             -> "whois.godaddy.com",
    "www.nic.ru/whois/en/"                           -> "whois.nic.ru",
    "www.whois.corporatedomains.com"                 -> "whois.corporatedomains.com",
    "www.safenames.net/DomainNames/WhoisSearch.aspx" -> "whois.safenames.net",
    "WWW.GNAME.COM/WHOIS"                            -> "whois.gname.com"
  )

  def determineWhoisServer(
    domain: String
  ): String = {
    val tld = DomainParser.getTLD(domain).getOrElse("")

    val server = whoisServerCache.getOrElseUpdate(tld, discoverWhoisServerForTLD(tld))

    misspelledWhoisServerMap.getOrElse(server, server)
  }

  private def discoverWhoisServerForTLD(
    tld: String
  ): String = {
    val defaultWhoisServer = "whois.iana.org"

    try {
      val queryHost = s"$tld.whois-servers.net"

      val lookup = new Lookup(queryHost, Type.CNAME)
      val result = lookup.run()

      val cnameTarget = Option(result).flatMap {
        records =>
          records.collectFirst {
            case cname: CNAMERecord => cname.getTarget.toString.stripSuffix(".")
          }
      }

      cnameTarget.getOrElse(defaultWhoisServer)
    } catch {
      case e: Throwable =>
        e.printStackTrace()
        defaultWhoisServer
    }
  }

}
