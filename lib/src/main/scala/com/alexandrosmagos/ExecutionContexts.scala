package com.alexandrosmagos

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

object ExecutionContexts {
  implicit val ioExecutionContext: ExecutionContext =
    ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
}
