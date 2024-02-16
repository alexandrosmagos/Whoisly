# WHOISLY - Scala WHOIS Library

A simple, non-blocking Scala library for performing WHOIS queries. This library allows you to query WHOIS servers asynchronously to retrieve domain registration information.

<p align="center">
  <img src="logo.png" width="400" alt="WHOISLY Logo"/>
</p>

## Features

- Asynchronous and synchronous WHOIS queries
- Automatic WHOIS server resolution for top-level domains (TLDs)
- Parsing of key domain registration details based on patterns found in the whoiser library
- Customizable query timeouts

## Getting Started

### Prerequisites

This library is built with Scala and requires the following:

- Scala 2.12 or higher
- sbt (Scala Build Tool) for building and managing dependencies

### Adding to Your Project

The library has still not been published.
To use the WHOISLY Scala Library in your project, build it and import it manually.

### Usage

#### Importing the Library

First, import the necessary components from the library:

```scala
import com.alexandrosmagos.Whoisly
import com.alexandrosmagos.ExecutionContexts.ioExecutionContext
```

You should use the library's `ExecutionContext` for typical use cases. However, if you have specific performance or resource management requirements, consider defining and using your own `ExecutionContext`.

#### Performing a WHOIS Query

You can perform asynchronous WHOIS queries using the `query` method:

```scala
val futureResponse = Whoisly.query("example.com")
futureResponse.onComplete {
  case Success(result) => println(s"Query Success: $result")
  case Failure(exception) => println(s"Query Failure: ${exception.getMessage}")
}
```

```scala
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

val futureResponse = Whoisly.query("example.org")

val result = Await.result(futureResponse, 10.seconds)
println(s"Result obtained using Await: $result")
```

For synchronous queries, use the `querySync` method:

```scala
val result = Whoisly.querySync("example.com")
println(s"Sync Query Result: $result")
```

### Contributing

Contributions to the WHOISLY Scala Library are welcome! Please submit pull requests or open issues to propose new features or report bugs.

### License

This library is open-sourced under the Apache-2.0 License. See the LICENSE file for more details.

### Acknowledgments

- Special thanks to the [whoiser JavaScript WHOIS library](https://github.com/LayeredStudio/whoiser) for the inspiration and logic behind WHOIS server resolution and data parsing.
- This library uses publicly available WHOIS servers and adheres to their usage policies.
