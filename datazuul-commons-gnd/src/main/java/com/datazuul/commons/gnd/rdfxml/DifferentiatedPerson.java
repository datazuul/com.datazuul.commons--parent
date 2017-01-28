package com.datazuul.commons.gnd.rdfxml;

import com.google.gson.Gson;

class DifferentiatedPerson {

  String dateOfBirth;
  String dateOfDeath;
  String firstname;
  String gndIdentifier;
  String surname;

  public String getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(String dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public String getDateOfDeath() {
    return dateOfDeath;
  }

  public void setDateOfDeath(String dateOfDeath) {
    this.dateOfDeath = dateOfDeath;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getGndIdentifier() {
    return gndIdentifier;
  }

  public void setGndIdentifier(String gndIdentifier) {
    this.gndIdentifier = gndIdentifier;
  }

  @Override
  public String toString() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

}
