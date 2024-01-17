# IFT2015 TP2

_By Etienne Collin & Emiliano Aviles_

## How to setup

Please note that the [stanford-corenlp-4.5.1 library](https://downloads.cs.stanford.edu/nlp/software/stanford-corenlp-4.5.1.zip) should be placed in the
`lib` directory of this project. This means that the structure of the project
should be as follows:

```
.
├── .idea
│   └── ...
├── dataset
│   ├── *.txt
│   └── ...
├── lib
│   └── stanford-corenlp-4.5.1
│       ├── *.jar
│       └── ...
├── query.txt
├── README.md
├── solution.txt
├── src
│   ├── *.java
│   └── ...
└── tp2.iml
```

## How to run

Open the IntelliJ project at the root of this directory (the directory that
contains this `README.md`) and run the `Main.java` class. Your queries should
have the right format and be placed in the `query.txt` file. The output of
those queries will be in the `solution.txt` file.
