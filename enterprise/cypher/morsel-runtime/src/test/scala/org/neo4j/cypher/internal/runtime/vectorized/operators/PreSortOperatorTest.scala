/*
 * Copyright (c) 2002-2017 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypher.internal.runtime.vectorized.operators

import org.neo4j.cypher.internal.compatibility.v3_4.runtime.slotted.pipes.Ascending
import org.neo4j.cypher.internal.runtime.vectorized.{Iteration, Morsel}
import org.neo4j.cypher.internal.compatibility.v3_4.runtime.{LongSlot, PipelineInformation}
import org.neo4j.cypher.internal.util.v3_4.symbols._
import org.neo4j.cypher.internal.util.v3_4.test_helpers.CypherFunSuite
import org.neo4j.values.AnyValue

class PreSortOperatorTest extends CypherFunSuite {
  test("sort a morsel with a single long column") {
    val slot = LongSlot(0, nullable = false, CTNode, "apa")
    val columnOrdering = Seq(Ascending(slot))
    val info = new PipelineInformation(Map("apa" -> slot), 1, 0)
    val sortOperator = new PreSortOperator(columnOrdering, info)

    val longs = Array[Long](9, 8, 7, 6, 5, 4, 3, 2, 1)
    val data = new Morsel(longs, Array[AnyValue](), longs.length)

    sortOperator.operate(new Iteration(None), data, null, null)

    data.longs should equal(Array[Long](1, 2, 3, 4, 5, 6, 7, 8, 9))
  }

  test("sort a morsel with a two long columns by one") {
    val slot1 = LongSlot(0, nullable = false, CTNode, "apa1")
    val slot2 = LongSlot(1, nullable = false, CTNode, "apa2")
    val columnOrdering = Seq(Ascending(slot1))
    val info = new PipelineInformation(Map("apa1" -> slot1, "apa2" -> slot2), 2, 0)
    val sortOperator = new PreSortOperator(columnOrdering, info)

    val longs = Array[Long](
      9, 0,
      8, 1,
      7, 2,
      6, 3,
      5, 4,
      4, 5,
      3, 6,
      2, 7,
      1, 8)
    val rows = longs.length / 2 // Since we have two columns per row
    val data = new Morsel(longs, Array[AnyValue](), rows)

    sortOperator.operate(new Iteration(None), data, null, null)

    data.longs should equal(Array[Long](
      1, 8,
      2, 7,
      3, 6,
      4, 5,
      5, 4,
      6, 3,
      7, 2,
      8, 1,
      9, 0)
    )
  }
}
