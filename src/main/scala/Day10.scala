package com.cannondev.advent

import scala.io.Source

case class State(cycle: Int, during: Int, after: Int, lastLine: Option[String])

object Day10:
  val resources = "src/main/resources"
  val index = 10
  val filePath = s"$resources/day_$index.txt"

  def move(lines: List[String], states: List[State]): List[State] =

    val noop = "noop".r
    val addx = "addx (.*)".r

    val line = lines.headOption

    val lastState = states.head

    val state = if (lastState.during == lastState.after) lastState else lastState.copy(during = lastState.after)

    line.map {
      case noop() => move(lines.tail, State(state.cycle + 1, state.during, state.after, line) :: states)
      case addx(v) =>
        move(
          lines.tail,
          State(state.cycle + 2, state.during, state.after + v.toInt, line) :: State(
            state.cycle + 1,
            state.during,
            state.after,
            line
          ) :: states
        )

      case e => throw new Error(s"invalid input: $e")
    } getOrElse states

  def getSignalStrength(state: State) =
    state.during * state.cycle

  def getSignalIntervals(cycles: Int): Seq[Int] =
    val length = cycles / 40
    if (cycles >= 20 && cycles < 40) List(20)
    else (0 until length).map(_ * 40).map(_ + 20)

  def isRegisterHere(state: State): String =
    if (((state.cycle-1) % 40 - state.during).abs < 2) "#" else "."

  def drawPixels(states: List[State]): List[String] =
    def initial: List[String] = ".".repeat(states.length+1).toList.map(_.toString)
    states.foldLeft(initial)((acc, state) =>
      val currentChar = isRegisterHere(state)
      if (state.cycle==10)
        println(state)
      acc.updated(state.cycle, currentChar)
    )

  def main(args: Array[String]): Unit =
    val fileContents = Source.fromFile(filePath).getLines().toList
    val initialStates = List(State(0, 1,1, None))
    val signals = move(fileContents, initialStates)
    val signalIntervals = getSignalIntervals(signals.length)
    val neededSignals = signalIntervals.flatMap(cycle => signals.find(_.cycle == cycle))
    val res = neededSignals.map(getSignalStrength)
    println(res)
    println(res.sum)
    val states = signals.reverse.tail

    val drawing = drawPixels(states).tail.grouped(40)
      drawing.foreach(l => println(l.mkString))
