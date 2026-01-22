CREATE TABLE public.user_stats_layouts (
    id bigserial PRIMARY KEY,
    user_id bigint NOT NULL UNIQUE REFERENCES public.users(id) ON DELETE CASCADE,
    layout_json text NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL
);

CREATE INDEX idx_user_stats_layouts_user_id ON public.user_stats_layouts (user_id);
