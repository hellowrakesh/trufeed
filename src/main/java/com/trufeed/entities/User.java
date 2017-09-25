package com.trufeed.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import java.util.Date;
import org.hibernate.validator.constraints.NotEmpty;

public class User extends LogSerializable {

  /** */
  private static final long serialVersionUID = 578254629453228515L;

  @NotEmpty private final String userName;
  private final String firstName;
  private final String lastName;

  @JsonCreator
  public User(
      @JsonProperty("uuid") String uuid,
      @JsonProperty("userName") String userName,
      @JsonProperty("firstName") String firstName,
      @JsonProperty("lastName") String lastName,
      @JsonProperty("createDate") Date createDate) {
    super(createDate, uuid);
    this.userName = userName;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public String getUserName() {
    return userName;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("uuid", uuid)
        .add("userName", userName)
        .add("firstName", firstName)
        .add("lastName", lastName)
        .add("createDate", createDate)
        .toString();
  }
}
