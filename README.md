Dotbot
------

Dotbot makes installing your dotfiles as easy as `git clone $url && cd dotfiles
&& ./install`, even on a freshly installed system!

Dotbot is a tool that bootstraps your dotfiles (it's a [Dot]files
[bo]o[t]strapper, get it?). It does *less* than you think, because version
control systems do more than you think.

Dotbot is designed to be lightweight and self-contained, with no external
dependencies and no installation required. Dotbot can also be a drop-in
replacement for any other tool you were using to manage your dotfiles, and
Dotbot is VCS-agnostic -- it doesn't make any attempt to manage your dotfiles.

## Setup

TODO - use gradle run or fat-jar.

## Full Example

Here's an example of a complete configuration.

The conventional name for the configuration file is `install.conf.yaml`.

```yaml
- defaults:
    link:
      relink: true

- clean: ['~']

- link:
    ~/.dotfiles: ''
    ~/.tmux.conf: tmux.conf
    ~/.vim: vim
    ~/.vimrc: vimrc

- shell:
  - [git submodule update --init --recursive, Installing submodules]
```

The configuration file can also be written in JSON. Here is the JSON equivalent
of the YAML configuration given above.

The conventional name for this file is `install.conf.json`.

```json
[
    {
        "defaults": {
            "link": {
                "relink": true
            }
        }
    },
    {
        "clean": ["~"]
    },
    {
        "link": {
            "~/.dotfiles": "",
            "~/.tmux.conf": "tmux.conf",
            "~/.vim": "vim",
            "~/.vimrc": "vimrc"
        }
    },
    {
        "shell": [
            ["git submodule update --init --recursive", "Installing submodules"]
        ]
    }
]
```

Configuration
-------------

Dotbot uses YAML or JSON formatted configuration files to let you specify how
to set up your dotfiles. Currently, Dotbot knows how to [link](#link) files and
folders, execute [shell](#shell) commands, and [clean](#clean) directories of
broken symbolic links. Dotbot also supports user [plugins](#plugins) for custom
commands.

**Ideally, bootstrap configurations should be idempotent. That is, the
installer should be able to be run multiple times without causing any
problems.** This makes a lot of things easier to do (in particular, syncing
updates between machines becomes really easy).

Dotbot configuration files are arrays of tasks, where each task
is a dictionary that contains a command name mapping to data for that command.
Tasks are run in the order in which they are specified. Commands within a task
do not have a defined ordering.

When writing nested constructs, keep in mind that YAML is whitespace-sensitive.
Following the formatting used in the examples is a good idea.

Also, note that `~` in YAML is the same as `null` in JSON. If you want a single
character string containing a tilde, make sure to enclose it in quotes: `'~'`

### Link

Link commands specify how files and directories should be symbolically linked.
If desired, items can be specified to be forcibly linked, overwriting existing
files if necessary. Environment variables in paths are automatically expanded.

#### Format

Link commands are specified as a dictionary mapping targets to source
locations. Source locations are specified relative to the base directory (that
is specified when running the installer). If linking directories, *do not* include a trailing slash.

Link commands support an (optional) extended configuration. In this type of
configuration, instead of specifying source locations directly, targets are
mapped to extended configuration dictionaries.

Available extended configuration parameters:

| Link Option | Explanation |
| -- | -- |
| `path` | The target for the symlink, the same as in the shortcut syntax (default:null, automatic (see below)) |
| `create` | When true, create parent directories to the link as needed. (default:false) |
| `relink` | Removes the old target if it's a symlink (default:false) |
| `force` | Force removes the old target, file or folder, and forces a new link (default:false) |
| `relative` | Use a relative path when creating the symlink (default:false, absolute links) |
| `glob` | Treat a `*` character as a wildcard, and perform link operations on all of those matches (default:false) |
| `if` | Execute this in your `$SHELL` and only link if it is successful. |

#### Example

```yaml
- link:
    ~/.config/terminator:
      create: true
      path: config/terminator
    ~/.vim: vim
    ~/.vimrc:
      relink: true
      path: vimrc
    ~/.zshrc:
      force: true
      path: zshrc
```

If the source location is omitted or set to `null`, Dotbot will use the
basename of the destination, with a leading `.` stripped if present. This makes
the following three config files equivalent:

```yaml
- link:
    ~/bin/ack: ack
    ~/.vim: vim
    ~/.vimrc:
      relink: true
      path: vimrc
    ~/.zshrc:
      force: true
      path: zshrc
    ~/.config/:
      glob: true
      path: config/*
      relink: true
```

```yaml
- link:
    ~/bin/ack:
    ~/.vim:
    ~/.vimrc:
      relink: true
    ~/.zshrc:
      force: true
    ~/.config/:
      glob: true
      path: config/*
      relink: true
```

```json
[
  {
    "link": {
      "~/bin/ack": null,
      "~/.vim": null,
      "~/.vimrc": {
        "relink": true
      },
      "~/.zshrc": {
        "force": true
      },
      "~/.config/": {
        "glob": true,
        "path": "config/*",
        "relink": true
      }
    }
  }
]
```

### Shell

Shell commands specify shell commands to be run. Shell commands are run in the
base directory (that is specified when running the installer).

#### Format

Shell commands can be specified in several different ways. The simplest way is
just to specify a command as a string containing the command to be run.

Another way is to specify a two element array where the first element is the
shell command and the second is an optional human-readable description.

Shell commands support an extended syntax as well, which provides more
fine-grained control. A command can be specified as a dictionary that contains
the command to be run, a description, and whether `stdin`, `stdout`, and
`stderr` are enabled. In this syntax, all keys are optional except for the
command itself.

#### Example

```yaml
- shell:
  - mkdir -p ~/src
  - [mkdir -p ~/downloads, Creating downloads directory]
  -
    command: read var && echo Your variable is $var
    stdin: true
    stdout: true
    description: Reading and printing variable
  -
    command: read fail
    stderr: true
```

### Clean

Clean commands specify directories that should be checked for dead symbolic
links. These dead links are removed automatically. Only dead links that point
to the dotfiles directory are removed unless the `force` option is set to
`true`.

#### Format

Clean commands are specified as an array of directories to be cleaned.

Clean commands support an extended configuration syntax. In this type of
configuration, commands are specified as directory paths mapping to options. If
the `force` option is set to `true`, dead links are removed even if they don't
point to a file inside the dotfiles directory.

#### Example

```yaml
- clean: ['~']

- clean:
    ~/.config:
      force: true
```

### Defaults

Default options for plugins can be specified so that options don't have to be
repeated many times. This can be very useful to use with the link command, for
example.

Defaults apply to all commands that follow setting the defaults. Defaults can
be set multiple times; each change replaces the defaults with a new set of
options.

#### Format

Defaults are specified as a dictionary mapping action names to settings, which
are dictionaries from option names to values.

#### Example

```yaml
- defaults:
    link:
      create: true
      relink: true
```

### Plugins

TODO


Contributing
------------

Do you have a feature request, bug report, or patch? Great! See
[CONTRIBUTING.md][contributing] for information on what you can do about that.

License
-------

Copyright (c) 2018 Steven Knight. Released under the MIT License. See
[LICENSE.md][license] for details.

Exceptions:

* The test directory contains code from the original dotbot project and
is used under of the terms of the MIT License.

* This README file contains text from the original dotbot project README
file and is used under of the terms of the MIT License.

[contributing]: CONTRIBUTING.md
[license]: LICENSE.md
