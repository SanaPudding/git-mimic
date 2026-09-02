# git-mimic

A simplified Git-like version-control system written in Java. It stores commits, a staging area, and branches inside a hidden `.gitlet` directory in the working tree, and is invoked as:

```bash
java gitlet.Main <command> [operands...]
```

This project is a standalone implementation of the [Gitlet](https://sp21.datastructur.es/materials/proj/proj2/proj2) design: a tiny subset of Git covering init, add, commit, checkout, branching, reset, and merge.

## Requirements

- JDK 11 or later
- Maven 3.6+ (optional, for building with `pom.xml`)

## Build

### javac

From the repository root:

```bash
javac -d out src/main/java/gitlet/*.java

java -cp out gitlet.Main init
java -cp out gitlet.Main add wug.txt
java -cp out gitlet.Main commit "added wug"
java -cp out gitlet.Main status
java -cp out gitlet.Main log
```

## Usage

### `init`

Creates a new repository in the current directory: `.gitlet` plus an initial commit on branch `master`.

```bash
java gitlet.Main init
```

Fails if a Gitlet repository already exists here.

### `add <file>`

Stages a file in the current working directory for the next commit. If the file was staged for removal, it is un-removed. Identical copies already tracked by the current commit are not re-staged.

```bash
java gitlet.Main add wug.txt
```

### `commit <message>`

Creates a new commit from the staging area. The new commit inherits the current commit’s tracked files, then applies staged additions and removals. The staging area is cleared afterward.

```bash
java gitlet.Main commit "added wug"
```

Requires a non-empty message and at least one staged change.

### `rm <file>`

Stops tracking a file. If it is tracked by the current commit, it is staged for removal and deleted from the working directory. If it is only staged for addition, it is unstaged.

```bash
java gitlet.Main rm wug.txt
```

### `log`

Prints the commit history of the current branch, newest first:

```
===
Commit <id>
<yyyy-MM-dd HH:mm:ss>
<message>
```

```bash
java gitlet.Main log
```

### `global-log`

Prints every commit in the repository, not just the current branch.

```bash
java gitlet.Main global-log
```

### `find <message>`

Prints the ids of all commits whose message equals the given string.

```bash
java gitlet.Main find "added wug"
```

### `status`

Shows branches (current branch marked with `*`), files staged for addition, and files staged for removal.

```bash
java gitlet.Main status
```

### `checkout`

Three forms:

Restore a file from the current commit:

```bash
java gitlet.Main checkout -- wug.txt
```

Restore a file from a specific commit (full or unique prefix of the id):

```bash
java gitlet.Main checkout 1a2b3c4 -- wug.txt
```

Switch to another branch (updates tracked files, moves `HEAD`, clears the staging area):

```bash
java gitlet.Main checkout other
```

Checkout of a branch or commit is refused if it would overwrite an untracked working-tree file.

### `branch <name>`

Creates a new branch pointing at the current commit. Does not switch to it.

```bash
java gitlet.Main branch other
```

### `rm-branch <name>`

Deletes the named branch pointer. Cannot delete the current branch.

```bash
java gitlet.Main rm-branch other
```

### `reset <commit-id>`

Moves the current branch to the given commit, checks out its files, and clears the staging area. Abbreviated ids are accepted when they uniquely identify a commit.

```bash
java gitlet.Main reset 1a2b3c4
```

### `merge <branch>`

Merges `branch` into the current branch.

- If the given branch is an ancestor of the current branch, nothing changes.
- If the current branch is an ancestor of the given branch, the current branch fast-forwards.
- Otherwise files are combined from the split point. Conflicting files are written with Git-style conflict markers:

```
<<<<<<< HEAD
current contents
=======
given-branch contents
>>>>>>>
```

The working tree and staging area must be clean, and the merge must not overwrite untracked files.

```bash
java gitlet.Main merge other
```
