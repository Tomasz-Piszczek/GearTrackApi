CREATE TABLE quote_attachments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quote_id UUID NOT NULL REFERENCES quotes(id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    file_data BYTEA NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    organization_id UUID NOT NULL,
    hidden BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_quote_attachments_quote_id ON quote_attachments(quote_id);
CREATE INDEX idx_quote_attachments_organization_id ON quote_attachments(organization_id);
