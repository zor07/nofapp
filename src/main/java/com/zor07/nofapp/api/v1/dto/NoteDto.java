package com.zor07.nofapp.api.v1.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class NoteDto {

  public Long id;
  public String title;
  public NotebookDto notebookDto;
  public JsonNode data;

}
