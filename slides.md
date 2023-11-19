# Agenda

- good practices
- performance boosts that come for free. And free is always a fair price.
- build up pyramid of understanding from vanilla hibernate to spring data jpa
- 45 Minutes limit ;)

# Why

- ORMs are easy to learn and extremely difficult to master
- JPA is stable, mature and opinionated
- 180 usages
- related notions: db connection, entity manager, transaction
- difficult to isolate technologies: Spring, Spring Data, Spring Boot, JDBC, JPA, Hibernate, JPL
- difficult to isolate threads, pools and caches: database connection, database connection pool, server thread pool,
  database-instance connections

# Introduction

Hibernate, JPA, JPL

- Hibernate implements JPA
- ...code: session
- JPL is a successor to JPL
- ...code: packets

# Vanilla Hibernate

Application for tennis court bookings.

# Vanilla Hibernate

ch01

SessionFactory - singleton
Session - a unit of work, lightweight, not thread safe
Transaction - a single session may have multiple transactions
Database Connection - lazy acquired by Session, 1:1 correspondence with Session

# Jakarta Persistence Layer

ch02

Completely wraps around Hibernate.
EntityManager = Session
EntityManagerFactory = SessionFactory

Hibernate has more rich API

# Flushes & Persistence Cache

ch03

## File: FlushesTest

Flushes happen

- before transaction commit
- before a query execution related to entities scheduled for update
- before native query execution

## File: FirstLevelCacheTest

First level cache can be though as a map of entity id to entity instance.

First Level Cache uses primary key equality.
There is an opened discussion whether to use surrogate keys or all fields.

# Find vs GetReference. Lazy Loading

ch04

## File: EmfBookingServiceTest

Compare:

- EagerLoading with find
- LazyLoading with getReference

Still ugly and far away from what we have in production code.

# Transaction Management in Spring

ch05

Proxies limitations

- Dynamic proxies require at lest one interface.
- CGLIB cannot proxy a non-public methods (?)

## File: DeclarativeTxBookingServiceTest

"Almost" production-like code.

## File: ExternalResourceWithingTxTest

We add complication - communication with external resource.

- DeclarativeTxBookingServiceExternal
- ProgrammatixTxBookingService

# Spring Data Basic Example

ch06

Note that in mature applications we will have a lot of injected repositories.
We may consider to inject EntityManager instead although this is controversial.

# Spring Web Container JPA management

# Notes / ideas

What happens if whe use multiple transactions
Validation - we can utilise Persistence Context for validation.
Query by example.