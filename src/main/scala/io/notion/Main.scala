package io.notion

import io.notion.program.Program
import zio._

object Main extends ZIOAppDefault {

  override def run = Program.run().onInterrupt(Console.printLine("Stopping the application").orDie).exitCode

}
