# ParagonFS

ParagonFS is a simple file system which is intended to back [Paragon](http://github.com/ianaldrighetti/paragon).

ParagonFS is simple because it only supports single level directory structures. This decision was made because Paragon
stores all tables in a single data directory. The Paragon database itself interacts with ParagonFS to store and retrieve
data.

To Paragon, a single row is stored within an individual file in ParagonFS. In ParagonFS we refer to a file as a _Paradigm_
(mainly just because it fits with the whole "Para" thing) &ndash; this Paradigm stores key/value pairs, where the key is
similar to that of a column name in a table. The value is anything assigned to it, which may be nothing at all.

## Storage

To keep things simple, and easily allow others to possibly use the data stored by ParagonFS, everything is stored in
JSON on disk. The file format is very simple:

    {
        "version": 1,
        "timestamp": {
            "created": "yyyy-MM-dd HH:mm:ss.S",
            "updated": "yyyy-MM-dd HH:mm:ss.S"
        }
        "data": {
            "{key 1 name}": {
                "type": "{data type}",
                "value": "{key 1 value}"
            }
        }
    }

The **version** field is the version of the Paradigm, or the number of times the Paradigm has been updated &ndash; 
_ParagonFS does not keep more than one version of a Paradigm_. The timestamps are self-explanatory &ndash; one for the
time at which it was originally created, the second for the last update timestamp (both are stored in UTC).

The **data** field is an object containing all the keys stored within the Paradigm. There is no defined limit on how many
keys a Paradigm can have. Each of the keys is an object which has a **type** field indicating the type of the value and
the **value** field contains the stringified value of the key.

## Creating / Finding a Paradigm

There are only a few operations which can be performed on a Paradigm. That is to create, update, delete and retrieve.
This is the interesting part: you can't name a file. When you create a file with ParagonFS all you can do is tell it
in which directory to create the file.

When a new Paradigm is requested ParagonFS will generate a random file name which becomes it's unique identifier across
the whole Paragon file system. ParagonFS will ensure that the generated file name is not used within any other directory
either. This is done so the file name can be the unique identifier to locate the file in any table with an index using
Paragon's indexing capabilities.

In addition to being able to create, update, delete and retrieve a Paradigm, you can also list all files within a
directory. Other than that, ParagonFS doesn't do much else. So once you create a Paradigm and you want to update it some
other time you will have to know the unique identifier for the Paradigm to retrieve and update it. The ability to find
a Paradigm be a key's value is not included within ParagonFS, as that's the responsibility of the indexer &ndash; which
doesn't exist yet.