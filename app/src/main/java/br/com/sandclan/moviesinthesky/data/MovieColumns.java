package br.com.sandclan.moviesinthesky.data;


import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.REAL;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface MovieColumns {

    @DataType(INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(INTEGER)
    @NotNull
    String ID_FROM_API = "id_from_api";

    @DataType(TEXT)
    @NotNull
    String TITLE = "title";

    @DataType(TEXT)
    @NotNull
    String ORIGINAL_TITLE = "original_title";

    @DataType(TEXT)
    @NotNull
    String IMAGE_URL = "image_url";

    @DataType(TEXT)
    @NotNull
    String SYNOPSIS = "synopsis";

    @DataType(REAL)
    @NotNull
    String VOTE_AVERAGE = "vote_average";

    @DataType(TEXT)
    @NotNull
    String RELEASE_DATE = "release_date";

    @DataType(INTEGER)
    @NotNull
    String FAVOURITE = "favourite";
}
