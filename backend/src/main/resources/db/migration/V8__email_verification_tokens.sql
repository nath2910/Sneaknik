CREATE TABLE public.email_verification_tokens (
    id bigserial PRIMARY KEY,
    user_id bigint NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    token character varying(200) NOT NULL UNIQUE,
    expires_at timestamp without time zone NOT NULL,
    used_at timestamp without time zone,
    created_at timestamp without time zone DEFAULT now() NOT NULL
);

CREATE INDEX idx_email_verification_tokens_user_id ON public.email_verification_tokens (user_id);
CREATE INDEX idx_email_verification_tokens_token ON public.email_verification_tokens (token);
