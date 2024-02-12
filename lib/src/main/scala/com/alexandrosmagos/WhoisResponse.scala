package com.alexandrosmagos

case class WhoisResponse(
  domain: String,
  response: whoisData,
  rawResponse: String,
  error: Option[String] = None)

case class whoisData(
  domainName: Option[String] = None,
  IDN: Option[String] = None,
  nameServers: List[String] = List.empty,
  domainStatus: List[String] = List.empty,
  registrarIANAID: Option[String] = None,
  registrar: Option[String] = None,
  registrarURL: Option[String] = None,
  registrarID: Option[String] = None,
  createdDate: Option[String] = None,
  updatedDate: Option[String] = None,
  expiryDate: Option[String] = None,
  registrantName: Option[String] = None,
  registrantEmail: Option[String] = None,
  registrantStreet: Option[String] = None,
  registrantCity: Option[String] = None,
  registrantCountry: Option[String] = None,
  registrantOrganization: Option[String] = None,
  registrantPhone: Option[String] = None,
  registrantStateProvince: Option[String] = None,
  DNSSEC: Option[String] = None)
