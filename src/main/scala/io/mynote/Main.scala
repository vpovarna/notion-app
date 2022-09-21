package io.mynote

import io.mynote.program.Program
import zio._

object Main extends ZIOAppDefault {

  override def run = Program.run().onInterrupt(Console.printLine("Stopping the application").orDie).exitCode

}
