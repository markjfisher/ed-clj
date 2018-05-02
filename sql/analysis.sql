-- this is all noodling, move anything real to util.sql

-- :name jl-ex1 :? :n
-- :doc pass in system-id and max to get details of systems within a distance and count
select
  s.name,
  round(cast (sqrt((s.x - px)^2 + (s.y - py)^2 + (s.z - pz)^2) as numeric), 2) as dist,
  (select count(*)
   from system_faction sf
   where sf.system_id = s.id
   group by sf.system_id) as fc,
  (select count(*)
   from faction f, system_faction sf
   where f.id = sf.minor_faction_id
         and sf.system_id = s.id
         and f.is_player_faction = true) as pf_cnt
from system s
  join (select x as px, y as py, z as pz from system ps where ps.name = 'Desy') as p on 1=1
where sqrt((x - px)^2 + (y - py)^2 + (z - pz)^2) < 20
      and (select count(*) from system_faction sf where sf.system_id = s.id group by sf.system_id) < 7
order by dist;

-- :name jl-ex2 :? :n
select
  s.name,
  s.population,
  round(cast (sqrt((s.x - px)^2 + (s.y - py)^2 + (s.z - pz)^2) as numeric), 2) as dist,
  (select count(*)
   from system_faction sf
   where sf.system_id = s.id
   group by sf.system_id) as fc,
  (select count(*)
   from faction f, system_faction sf
   where f.id = sf.minor_faction_id
         and sf.system_id = s.id
         and f.is_player_faction = true) as pf_cnt,
  (select max(sf.influence)
   from system_faction sf
   where sf.system_id = s.id) as max_inf
from system s
  join (select x as px,
               y as py,
               z as pz
        from system ps
        where ps.name = 'Cardea') as p on 1=1
where sqrt((x - px)^2 + (y - py)^2 + (z - pz)^2) < 20
      and (select count(*) from system_faction sf where sf.system_id = s.id group by sf.system_id) < 7
      and s.population < 200000
      and (select count(*) from faction f, system_faction sf where f.id = sf.minor_faction_id and sf.system_id = s.id and f.is_player_faction = true) = 0
      and (select max(sf.influence) from system_faction sf where sf.system_id = s.id) > 60
order by dist;


-- :name jl-ex3 :? :n

-- example of doing query over a range of values
-- with oom (name) as (values ('Apathaam'), ('Aurgel'), ('Azrael'), ('Cardea'), ('Exioce'), ('Njiri'), ('NLTT 10055'))

drop function jl_query(character varying);
create or replace function
  jl_query(
  _system_name VARCHAR(200),
  _min_inf integer,
  _max_dist numeric,
  _max_population integer
)
  returns table (
    sys  VARCHAR(200),
    name VARCHAR(200),
    population BIGINT,
    dist NUMERIC,
    fc BIGINT,
    pf_cnt BIGINT,
    max_inf REAL
  ) as
$func$
begin
  return query
  select
    _system_name,
    s.name,
    s.population,
    round(cast (sqrt((s.x - px)^2 + (s.y - py)^2 + (s.z - pz)^2) as numeric), 2) as dist,
    (select count(*)
     from system_faction sf
     where sf.system_id = s.id
     group by sf.system_id) as fc,
    (select count(*)
     from faction f, system_faction sf
     where f.id = sf.minor_faction_id
           and sf.system_id = s.id
           and f.is_player_faction = true) as pf_cnt,
    (select max(sf.influence)
     from system_faction sf
     where sf.system_id = s.id) as max_inf
  from system s
    inner join (select x as px,
                       y as py,
                       z as pz
                from system ps
                where ps.name = _system_name) as p on 1=1
  where sqrt((x - px)^2 + (y - py)^2 + (z - pz)^2) < _max_dist
        and (select count(*) from system_faction sf where sf.system_id = s.id group by sf.system_id) < 7
        and s.population < _max_population
        and (select count(*) from faction f, system_faction sf where f.id = sf.minor_faction_id and sf.system_id = s.id and f.is_player_faction = true) = 0
        and (select max(sf.influence) from system_faction sf where sf.system_id = s.id) > _min_inf
  order by dist;
end
$func$ LANGUAGE plpgsql;

drop function jl_query2(character varying);
create or replace function
  jl_query2(
  _system_name VARCHAR(200),
  _max_dist numeric
)
  returns table (
    sys  VARCHAR(200),
    name VARCHAR(200),
    population BIGINT,
    dist NUMERIC,
    fc BIGINT,
    pf_cnt BIGINT
  ) as
$func$
begin
  return query
  select
    _system_name,
    s.name,
    s.population,
    round(cast (sqrt((s.x - px)^2 + (s.y - py)^2 + (s.z - pz)^2) as numeric), 2) as dist,
    (select count(*)
     from system_faction sf
     where sf.system_id = s.id
     group by sf.system_id) as fc,
    (select count(*)
     from faction f, system_faction sf
     where f.id = sf.minor_faction_id
           and sf.system_id = s.id
           and f.is_player_faction = true) as pf_cnt
  from system s
    inner join (select x as px,
                       y as py,
                       z as pz
                from system ps
                where ps.name = _system_name) as p on 1=1
  where sqrt((x - px)^2 + (y - py)^2 + (z - pz)^2) < _max_dist
        and (select count(*) from system_faction sf where sf.system_id = s.id group by sf.system_id) < 7
        and (select count(*) from faction f, system_faction sf where f.id = sf.minor_faction_id and sf.system_id = s.id and f.is_player_faction = true) > 0
        and s.name != _system_name
  order by dist;
end
$func$ LANGUAGE plpgsql;


-- much simpler, within a distance of named system
DROP FUNCTION jl_query3(character varying, numeric);
create or replace function
  jl_query3(
  _system_name VARCHAR(200),
  _max_dist numeric
)
  returns table (
    sys  VARCHAR(200),
    name VARCHAR(200),
    population BIGINT,
    dist NUMERIC,
    fc BIGINT,
    pf_cnt BIGINT
  ) as
$func$
begin
  return query
  select
    _system_name,
    s.name,
    s.population,
    round(cast (sqrt((s.x - px)^2 + (s.y - py)^2 + (s.z - pz)^2) as numeric), 2) as dist,
    (select count(*)
     from system_faction sf
     where sf.system_id = s.id
     group by sf.system_id) as fc,
    (select count(*)
     from faction f, system_faction sf
     where f.id = sf.minor_faction_id
           and sf.system_id = s.id
           and f.is_player_faction = true) as pf_cnt
  from system s
    inner join (select x as px,
                       y as py,
                       z as pz
                from system ps
                where ps.name = _system_name) as p on 1=1
  where sqrt((x - px)^2 + (y - py)^2 + (z - pz)^2) < _max_dist
        and (select count(*) from system_faction sf where sf.system_id = s.id group by sf.system_id) < 7
  order by dist;
end
$func$ LANGUAGE plpgsql;

with oom (name) as (values ('Apathaam'), ('Aurgel'), ('Azrael'), ('Cardea'), ('Exioce'), ('Njiri'), ('NLTT 10055')) select (jl_query3(name, 20.0)).* from oom;

with oom (name) as (values ('Apathaam'), ('Aurgel'), ('Azrael'), ('Cardea'), ('Exioce'), ('Njiri'), ('NLTT 10055')) select (jl_query2(name)).* from oom;
with mcrn (name) as (values ('Alberta'), ('Canonnia'), ('Desy'), ('Dubbuennel'), ('Eol Prou LW-L c8-83'), ('Kioti 368'), ('Mobia'), ('Pytheas')) select (jl_query(name, 10, 20.0, 100000000)).* from mcrn;


-- systems within _max_dist of _system_name in expansion state, or with over _min_inf influence

DROP FUNCTION jl_query4(character varying,integer,numeric);
create or replace function
  jl_query4(
  _system_name VARCHAR(200),
  _min_inf integer,
  _max_dist numeric
)
  returns table (
    sys  VARCHAR(200),
    name VARCHAR(200),
    population BIGINT,
    dist NUMERIC,
    fc BIGINT,
    pf_cnt BIGINT,
    max_inf REAL,
    state INTEGER
  ) as
$func$
begin
  return query
  select
    _system_name,
    s.name,
    s.population,
    round(cast (sqrt((s.x - px)^2 + (s.y - py)^2 + (s.z - pz)^2) as numeric), 2) as dist,
    (select count(*)
     from system_faction sf
     where sf.system_id = s.id
     group by sf.system_id) as fc,
    (select count(*)
     from faction f, system_faction sf
     where f.id = sf.minor_faction_id
           and sf.system_id = s.id
           and f.is_player_faction = true) as pf_cnt,
    (select max(sf.influence)
     from system_faction sf
     where sf.system_id = s.id) as max_inf,
    s.state_id as state
  from system s
    inner join (select x as px,
                       y as py,
                       z as pz
                from system ps
                where ps.name = _system_name) as p on 1=1
  where sqrt((x - px)^2 + (y - py)^2 + (z - pz)^2) < _max_dist
        and (select count(*) from system_faction sf where sf.system_id = s.id group by sf.system_id) < 9
        and ((select max(sf.influence) from system_faction sf where sf.system_id = s.id) > _min_inf
             or s.state_id = 67)
  order by dist;
end
$func$ LANGUAGE plpgsql;

with oom (name) as (values ('Apathaam'), ('Cardea')) select (jl_query4(name, 10, 21.0)).* from oom;


-- case
-- when s.state_id = 16 then 'Boom'
-- when s.state_id = 32 then 'Bust'
-- when s.state_id = 48 then 'Civil Unrest'
-- when s.state_id = 64 then 'Civil War'
-- when s.state_id = 65 then 'Election'
-- when s.state_id = 67 then 'Expansion'
-- when s.state_id = 37 then 'Famine'
-- when s.state_id = 101 then 'Investment'
-- when s.state_id = 69 then 'Lockdown'
-- when s.state_id = 80 then 'None'
-- when s.state_id = 96 then 'Retreat'
-- when s.state_id = 73 then 'War'
-- else 'Unknown'
-- end as state
--
--
-- (select case
--         when (ss.state_id = 16) then 'Boom'
--         when (ss.state_id = 32) then 'Bust'
--         when (ss.state_id = 48) then 'Civil Unrest'
--         when (ss.state_id = 64) then 'Civil War'
--         when (ss.state_id = 65) then 'Election'
--         when (ss.state_id = 67) then 'Expansion'
--         when (ss.state_id = 37) then 'Famine'
--         when (ss.state_id = 101) then 'Investment'
--         when (ss.state_id = 69) then 'Lockdown'
--         when (ss.state_id = 80) then 'None'
--         when (ss.state_id = 96) then 'Retreat'
--         when (ss.state_id = 73) then 'War'
--         else 'Unknown'
--         end
--  from system ss
--  where ss.state_id = s.state_id
--        and ss.id = s.id) as state

DROP FUNCTION ed_within(character varying,numeric);
create or replace function
  ed_within(
  _system_name VARCHAR(200),
  _max_dist numeric
)
  returns table (
    sys  VARCHAR(200),
    name VARCHAR(200),
    pop BIGINT,
    d NUMERIC,
    fc BIGINT,
    pc BIGINT,
    inf NUMERIC
  ) as
$func$
begin
  return query
  select
    _system_name,
    s.name,
    s.population,
    round(cast (sqrt((s.x - px)^2 + (s.y - py)^2 + (s.z - pz)^2) as numeric), 2) as dist,
    (select count(*)
     from system_faction sf
     where sf.system_id = s.id
     group by sf.system_id) as fc,
    (select count(*)
     from faction f, system_faction sf
     where f.id = sf.minor_faction_id
           and sf.system_id = s.id
           and f.is_player_faction = true) as pc,
    (select round(cast (max(sf.influence) as numeric), 1)
     from system_faction sf
     where sf.system_id = s.id) as inf
  from system s
    inner join (select x as px,
                       y as py,
                       z as pz
                from system ps
                where ps.name = _system_name) as p on 1=1
  where sqrt((x - px)^2 + (y - py)^2 + (z - pz)^2) < _max_dist
  order by dist;
end
$func$ LANGUAGE plpgsql;
