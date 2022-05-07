package sk.sandeep.newsapp.db

import androidx.room.TypeConverter
import sk.sandeep.newsapp.model.Source


class SourceConverters {

    @TypeConverter
    fun fromSource(source: Source) : String{
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source{
        return Source(name,name)
    }
}