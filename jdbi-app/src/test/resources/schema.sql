drop table if exists item;
drop table if exists cart;

--
-- Item table
--
CREATE TABLE item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name STRING NOT NULL,
    description STRING NOT NULL
);

--
-- Cart table
--
CREATE TABLE cart (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    username STRING NOT NULL, 
    item UUID NOT NULL REFERENCES item(id), 
    quantity int NOT NULL
);

