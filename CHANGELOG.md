# Change Log
Added this change log after project was already under way.  Early changes are not noted.

The format is based on [Keep a Changelog](http://keepachangelog.com/).

## [Unreleased]
### Changed
- added handling of Kotlin `Sequence`

## [2.1] - 2019-02-10
### Changed
- added shortcut method `toJSON()` to `JSONSerializer` to output directly to string
- added shortcut methods `parse()` to `JSONDeserializer` to parse directly from string

## [2.0] - 2019-02-10
### Changed
- switched to Java 8; added handling of `java.time` classes and `Optional`
- added IntelliJ files to `.gitignore`
- added handling of `Enumeration`, `Iterator` and `Iterable`

## [1.0] - 2017-04-17
### Added
- new class `JSONSerializer`
- new class `JSONDeserializer`
- new annotation `@JSONIgnore`
- new annotation `@JSONAlways`
- new annotation `@JSONName`
