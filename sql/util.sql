
DROP FUNCTION ed_within(character varying,numeric);

CREATE OR REPLACE FUNCTION ed_within(_system_name character varying, _max_dist numeric)
  RETURNS TABLE (
    sys character varying,
    name character varying,
    pop bigint,
    d numeric,
    fc bigint,
    pc bigint,
    inf numeric
  )
LANGUAGE plpgsql
AS $function$
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
$function$;
