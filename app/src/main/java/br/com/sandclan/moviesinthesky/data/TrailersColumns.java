package br.com.sandclan.moviesinthesky.data;


import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface TrailersColumns {

    @DataType(INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(TEXT)
    @NotNull
    String ID_FROM_API = "id_from_api";

    @DataType(TEXT)
    @NotNull
    String MOVIE_ID = "movie_id";

    @DataType(TEXT)
    @NotNull
    String NAME = "name";

    @DataType(TEXT)
    @NotNull
    String SITE = "site";

    @DataType(INTEGER)
    @NotNull
    String SIZE = "size";

    @DataType(TEXT)
    @NotNull
    String LANGUAGE = "language";

    @DataType(TEXT)
    @NotNull
    String KEY = "key";

    @DataType(TEXT)
    @NotNull
    String TYPE = "type";


}
