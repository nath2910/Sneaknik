package backend.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class StatsLayoutRequest {
  private JsonNode layout;

  public JsonNode getLayout() { return layout; }
  public void setLayout(JsonNode layout) { this.layout = layout; }
}
