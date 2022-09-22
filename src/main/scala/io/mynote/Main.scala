package io.mynote

import io.mynote.console.Program
import zio._

object Main extends ZIOAppDefault {

  override def run = Program.run().onInterrupt(Console.printLine("Stopping the application").orDie).exitCode

}
