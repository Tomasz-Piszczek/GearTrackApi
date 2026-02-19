CREATE TABLE tool_groups (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    organization_id UUID NOT NULL,
    hidden BOOLEAN NOT NULL DEFAULT FALSE,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT fk_tool_groups_organization FOREIGN KEY (organization_id) REFERENCES organizations(id)
);

ALTER TABLE tools ADD COLUMN tool_group_id UUID;

ALTER TABLE tools ADD CONSTRAINT fk_tools_tool_group
    FOREIGN KEY (tool_group_id) REFERENCES tool_groups(id);

CREATE INDEX idx_tool_groups_organization_id ON tool_groups(organization_id);
CREATE INDEX idx_tools_tool_group_id ON tools(tool_group_id);

DO $$
DECLARE
    org_record RECORD;
    new_group_id UUID;
BEGIN
    FOR org_record IN SELECT DISTINCT organization_id FROM tools WHERE organization_id IS NOT NULL
    LOOP
        INSERT INTO tool_groups (organization_id, name)
        VALUES (org_record.organization_id, 'Klucze')
        RETURNING id INTO new_group_id;

        UPDATE tools
        SET tool_group_id = new_group_id
        WHERE organization_id = org_record.organization_id;
    END LOOP;
END $$;
