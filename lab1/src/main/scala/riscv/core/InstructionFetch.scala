// Copyright 2021 Howard Lau
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package riscv.core

import chisel3._
import peripheral.{RamAccessBundle}
import riscv.Parameters

object ProgramCounter {
  val EntryAddress = Parameters.EntryAddress
}

class InstructionFetch extends Module {
  val io = IO(new Bundle {
    val jump_flag_id = Input(Bool())
    val jump_address_id = Input(UInt(Parameters.AddrWidth))

    val id_instruction_address = Output(UInt(Parameters.AddrWidth))
    val id_instruction = Output(UInt(Parameters.InstructionWidth))

    val bundle = new RamAccessBundle
  })
  val pc = RegInit(ProgramCounter.EntryAddress)
  pc := Mux(
    io.jump_flag_id,
    io.jump_address_id,
    pc + 4.U,
  )

  io.bundle.address := pc
  io.bundle.write_strobe := VecInit(Seq.fill(Parameters.WordSize)(false.B))
  io.bundle.write_enable := false.B
  io.bundle.write_data := 0.U

  io.id_instruction := io.bundle.read_data
  io.id_instruction_address := pc
}
