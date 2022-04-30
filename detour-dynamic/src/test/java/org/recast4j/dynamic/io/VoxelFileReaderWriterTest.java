/*
recast4j copyright (c) 2021 Piotr Piastucki piotr@jtilia.org

This software is provided 'as-is', without any express or implied
warranty.  In no event will the authors be held liable for any damages
arising from the use of this software.
Permission is granted to anyone to use this software for any purpose,
including commercial applications, and to alter it and redistribute it
freely, subject to the following restrictions:
1. The origin of this software must not be misrepresented; you must not
 claim that you wrote the original software. If you use this software
 in a product, an acknowledgment in the product documentation would be
 appreciated but is not required.
2. Altered source versions must be plainly marked as such, and must not be
 misrepresented as being the original software.
3. This notice may not be removed or altered from any source distribution.
*/

package org.recast4j.dynamic.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class VoxelFileReaderWriterTest {

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    public void shouldReadSingleTileFile(boolean compression) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("test.voxels")) {
            VoxelFile f = readWriteRead(is, compression);
            assertThat(f.useTiles).isFalse();
            assertThat(f.bounds).containsExactly(-100.0f, 0f, -100f, 100f, 5f, 100f);
            assertThat(f.cellSize).isEqualTo(0.25f);
            assertThat(f.walkableRadius).isEqualTo(0.5f);
            assertThat(f.walkableHeight).isEqualTo(2f);
            assertThat(f.walkableClimb).isEqualTo(0.5f);
            assertThat(f.maxEdgeLen).isEqualTo(20f);
            assertThat(f.maxSimplificationError).isEqualTo(2f);
            assertThat(f.minRegionArea).isEqualTo(2f);
            assertThat(f.regionMergeArea).isEqualTo(12f);
            assertThat(f.tiles.size()).isEqualTo(1);
            assertThat(f.tiles.get(0).cellHeight).isEqualTo(0.001f);
            assertThat(f.tiles.get(0).width).isEqualTo(810);
            assertThat(f.tiles.get(0).depth).isEqualTo(810);
            assertThat(f.tiles.get(0).spanData).hasSize(9021024);
            assertThat(f.tiles.get(0).boundsMin).containsExactly(-101.25f, 0f, -101.25f);
            assertThat(f.tiles.get(0).boundsMax).containsExactly(101.25f, 5.0f, 101.25f);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    public void shouldReadMultiTileFile(boolean compression) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("test_tiles.voxels")) {
            VoxelFile f = readWriteRead(is, compression);
            assertThat(f.useTiles).isTrue();
            assertThat(f.bounds).containsExactly(-100.0f, 0f, -100f, 100f, 5f, 100f);
            assertThat(f.cellSize).isEqualTo(0.25f);
            assertThat(f.walkableRadius).isEqualTo(0.5f);
            assertThat(f.walkableHeight).isEqualTo(2f);
            assertThat(f.walkableClimb).isEqualTo(0.5f);
            assertThat(f.maxEdgeLen).isEqualTo(20f);
            assertThat(f.maxSimplificationError).isEqualTo(2f);
            assertThat(f.minRegionArea).isEqualTo(2f);
            assertThat(f.regionMergeArea).isEqualTo(12f);
            assertThat(f.tiles.size()).isEqualTo(100);
            assertThat(f.tiles.get(0).cellHeight).isEqualTo(0.001f);
            assertThat(f.tiles.get(0).width).isEqualTo(90);
            assertThat(f.tiles.get(0).depth).isEqualTo(90);
            assertThat(f.tiles.get(0).spanData).hasSize(104952);
            assertThat(f.tiles.get(5).spanData).hasSize(109080);
            assertThat(f.tiles.get(18).spanData).hasSize(113400);
            assertThat(f.tiles.get(0).boundsMin).containsExactly(-101.25f, 0f, -101.25f);
            assertThat(f.tiles.get(0).boundsMax).containsExactly(-78.75f, 5.0f, -78.75f);
        }
    }

    private VoxelFile readWriteRead(InputStream is, boolean compression) throws IOException {
        VoxelFileReader reader = new VoxelFileReader();
        VoxelFile f = reader.read(is);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VoxelFileWriter writer = new VoxelFileWriter();
        writer.write(out, f, compression);
        return reader.read(new ByteArrayInputStream(out.toByteArray()));
    }

}
