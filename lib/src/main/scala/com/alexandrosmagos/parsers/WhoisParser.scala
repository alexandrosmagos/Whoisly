package com.alexandrosmagos.parsers

import com.alexandrosmagos.whoisData

object WhoisParser {

  // Terms indicating no useful data
  private val noData: Set[String] = Set(
    "-",
    ".",
    "n/a",
    "no data",
    "redacted",
    "privado",
    "datos privados",
    "data protected",
    "not disclosed",
    "data protected, not disclosed",
    "data redacted",
    "not disclosed not disclosed",
    "not disclosed! visit www.eurid.eu for webbased whois.",
    "not available",
    "redacted for privacy",
    "redacted | eu data subject",
    "gdpr redacted",
    "non-public data",
    "gdpr masked",
    "statutory masking enabled",
    "redacted by privacy",
    "not applicable",
    "na",
    "redacted for privacy purposes",
    "redacted | eu registrar",
    "registration private",
    "none",
    "redacted.forprivacy",
    "redacted | registry policy",
    "redacted for gdpr privacy",
    "redacted for gdpr",
    "redacted redacted",
    "not available from registry"
  )

  // Phrases indicating a lack of data or non-existence of the domain
  private val errorPhrases: Set[String] = Set(
    "Domain not found.",
    "No match for",
    "returned 0 objects",
    "No match found"
  )

  // Map of labels to be renamed
  private val renameLabels: Map[String, String] = Map(
    "domain name"                            -> "Domain Name",
    "domain"                                 -> "Domain Name",
    "domain..............."                  -> "Domain Name",             // found in .ax
    "idn tag"                                -> "IDN",
    "internationalized domain name"          -> "IDN",
    "name server"                            -> "Name Server",
    "nameserver"                             -> "Name Server",
    "nameservers"                            -> "Name Server",
    "nserver"                                -> "Name Server",
    "name servers"                           -> "Name Server",
    "name server information"                -> "Name Server",
    "dns"                                    -> "Name Server",
    "nserver.............."                  -> "Name Server",             // found in .ax
    "hostname"                               -> "Name Server",
    "domain nameservers"                     -> "Name Server",
    "domain servers in listed order"         -> "Name Server",             // found in .ly
    "name servers dns"                       -> "Name Server",             // found in .mx
    "Domain Status"                          -> "Domain Status",
    "flags"                                  -> "Domain Status",
    "status"                                 -> "Domain Status",
    "state"                                  -> "Domain Status",           // found in .ru
    "registration status"                    -> "Domain Status",
    "registrar iana id"                      -> "Registrar IANA ID",
    "sponsoring registrar iana id"           -> "Registrar IANA ID",
    "registrar"                              -> "Registrar",
    "organisation"                           -> "Registrar",
    "registrar name"                         -> "Registrar",
    "registrar organization"                 -> "Registrar",
    "registrar............"                  -> "Registrar",               // found in .ax
    "record maintained by"                   -> "Registrar",
    "sponsoring registrar"                   -> "Registrar",
    "registrar url"                          -> "Registrar URL",           // found in .it
    "registrar web"                          -> "Registrar URL",           // found in .it
    "url"                                    -> "Registrar URL",
    "registrar website"                      -> "Registrar URL",
    "www.................."                  -> "Registrar URL",           // found in .ax
    "mnt-by"                                 -> "Registrar ID",            // found in .ua
    "created date"                           -> "Created Date",
    "creation date"                          -> "Created Date",
    "registered on"                          -> "Created Date",
    "registration date"                      -> "Created Date",
    "relevant dates registered on"           -> "Created Date",
    "created"                                -> "Created Date",
    "created on"                             -> "Created Date",            // found in .mx
    "registration time"                      -> "Created Date",
    "registered"                             -> "Created Date",
    "created.............."                  -> "Created Date",            // found in .ax
    "domain registered"                      -> "Created Date",
    "registered date"                        -> "Created Date",            // found in .co.jp
    "updated date"                           -> "Updated Date",
    "last updated"                           -> "Updated Date",
    "changed"                                -> "Updated Date",
    "modified"                               -> "Updated Date",
    "updated"                                -> "Updated Date",            // found in .ly
    "modification date"                      -> "Updated Date",
    "last modified"                          -> "Updated Date",
    "relevant dates last updated"            -> "Updated Date",            // found in .uk, .co.uk
    "last updated on"                        -> "Updated Date",            // found in .mx
    "last update"                            -> "Updated Date",            // found in .co.jp
    "expiry date"                            -> "Expiry Date",
    "expire date"                            -> "Expiry Date",
    "registrar registration expiration date" -> "Expiry Date",
    "registry expiry date"                   -> "Expiry Date",
    "expires on"                             -> "Expiry Date",
    "expires"                                -> "Expiry Date",
    "expiration time"                        -> "Expiry Date",
    "expiration date"                        -> "Expiry Date",
    "expires.............."                  -> "Expiry Date",             // found in .ax
    "paid-till"                              -> "Expiry Date",
    "expiry date"                            -> "Expiry Date",
    "relevant dates expiry date"             -> "Expiry Date",             // found in .uk, .co.uk
    "record will expire on"                  -> "Expiry Date",
    "expired"                                -> "Expiry Date",             // found in .ly
    "registry registrant id"                 -> "Registry Registrant ID",  // found in .ai
    "registry registrantid"                  -> "Registry Registrant ID",  // found in .ai
    "registrant name"                        -> "Registrant Name",         // found in .ai
    "registrant"                             -> "Registrant Name",         // found in .ai
    "registrant contact name"                -> "Registrant Name",
    "registrantname"                         -> "Registrant Name",         // found in .ai
    "registrant person"                      -> "Registrant Name",         // found in .ua
    "registrant email"                       -> "Registrant Email",        // found in .ua
    "registrant contact email"               -> "Registrant Email",
    "registrantemail"                        -> "Registrant Email",        // found in .ai
    "registrant street"                      -> "Registrant Street",       // found in .ai
    "registrantstreet"                       -> "Registrant Street",       // found in .ai
    "registrant's address"                   -> "Registrant Street",
    "registrant city"                        -> "Registrant City",         // found in .ai
    "registrantcity"                         -> "Registrant City",         // found in .ai
    "registrant country"                     -> "Registrant Country",      // found in .ai
    "registrantcountry"                      -> "Registrant Country",      // found in .ai
    "registrant organisation"                -> "Registrant Organization",
    "registrant phone"                       -> "Registrant Phone",
    "registrantphone"                        -> "Registrant Phone",
    "registrant organization"                -> "Registrant Organization", // found in .uk, .co.uk
    "trading as"                             -> "Registrant Organization", // found in .uk, .co.uk
    "org"                                    -> "Registrant Organization", // found in .ru
    "registrant state"                       -> "Registrant State/Province",
    "dnssec"                                 -> "DNSSEC"
  )

  def parse(
    rawWhoisData: String
  ): (whoisData, Option[String]) = {
    val lines = rawWhoisData.split("\n")

    val containsError = errorPhrases.exists(phrase => rawWhoisData.contains(phrase))

    val parsedData = if (!containsError) {
      lines.foldLeft(Map.empty[String, List[String]]) {
        case (acc, line) =>
          line.split(":", 2) match {
            case Array(label, value) if value.trim.nonEmpty && !noData.contains(value.trim.toLowerCase) =>
              val cleanedLabel = label.trim.toLowerCase
              val finalLabel   = renameLabels.getOrElse(cleanedLabel, cleanedLabel.capitalize)
              acc.updated(finalLabel, acc.getOrElse(finalLabel, List.empty) :+ value.trim)
            case _                                                                                      => acc
          }
      }
    } else Map.empty[String, List[String]]

    val error = if (errorPhrases.exists(phrase => rawWhoisData.contains(phrase)) || rawWhoisData.trim.isEmpty) {
      Some("No data available for the domain")
    } else None

    (whoisData(
       domainName = parsedData.get("Domain Name").flatMap(_.headOption),
       IDN = parsedData.get("IDN").flatMap(_.headOption),
       nameServers = parsedData.getOrElse("Name Server", List.empty),
       domainStatus = parsedData.getOrElse("Domain Status", List.empty),
       registrarIANAID = parsedData.get("Registrar IANA ID").flatMap(_.headOption),
       registrar = parsedData.get("Registrar").flatMap(_.headOption),
       registrarURL = parsedData.get("Registrar URL").flatMap(_.headOption),
       createdDate = parsedData.get("Created Date").flatMap(_.headOption),
       updatedDate = parsedData.get("Updated Date").flatMap(_.headOption),
       expiryDate = parsedData.get("Expiry Date").flatMap(_.headOption),
       registrantName = parsedData.get("Registrant Name").flatMap(_.headOption),
       registrantEmail = parsedData.get("Registrant Email").flatMap(_.headOption),
       registrantStreet = parsedData.get("Registrant Street").flatMap(_.headOption),
       registrantCity = parsedData.get("Registrant City").flatMap(_.headOption),
       registrantCountry = parsedData.get("Registrant Country").flatMap(_.headOption),
       registrantOrganization = parsedData.get("Registrant Organization").flatMap(_.headOption),
       registrantPhone = parsedData.get("Registrant Phone").flatMap(_.headOption),
       registrantStateProvince = parsedData.get("Registrant State").flatMap(_.headOption),
       DNSSEC = parsedData.get("DNSSEC").flatMap(_.headOption)
     ),
     error
    )
  }

}
