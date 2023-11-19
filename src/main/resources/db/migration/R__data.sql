insert into court (id, name)
values ('10969772-192d-40c1-b3a3-98f15db85d2e', 'first-court'),
       ('e63ee907-afb8-4b5c-be70-ce54994cb266', 'second-court')
on conflict (id) do update set name = excluded.name;

insert into player(id, email)
values ('11610ec3-9187-4bcc-a0f6-39fa8437677d', 'first-player@invalid.com'),
       ('f4f016e8-7402-4b40-95c1-fb4ffa798a59', 'second-player@notexisting.com'),
       ('e97537d2-4104-4633-88b4-181624873f64', 'third-player@perished.com'),
       ('5e95f04b-187f-4023-9754-808d4252bca4', 'fourth-player@invalid.com')
on conflict (id) do update set email = excluded.email;
