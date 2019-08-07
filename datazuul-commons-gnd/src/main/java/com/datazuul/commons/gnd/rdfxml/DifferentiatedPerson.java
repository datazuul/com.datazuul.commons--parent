package com.datazuul.commons.gnd.rdfxml;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

class DifferentiatedPerson {

  String dateOfBirth;
  String dateOfDeath;
  String firstname;
  String gndIdentifier;
  String placeOfBirthUrl;
  String placeOfDeathUrl;
  List<String> professions = new ArrayList();
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

  public String getGndIdentifier() {
    return gndIdentifier;
  }

  public void setGndIdentifier(String gndIdentifier) {
    this.gndIdentifier = gndIdentifier;
  }

  public String getPlaceOfBirthUrl() {
    return placeOfBirthUrl;
  }

  public void setPlaceOfBirthUrl(String placeOfBirthUrl) {
    this.placeOfBirthUrl = placeOfBirthUrl;
  }

  public String getPlaceOfDeathUrl() {
    return placeOfDeathUrl;
  }

  public void setPlaceOfDeathUrl(String placeOfDeathUrl) {
    this.placeOfDeathUrl = placeOfDeathUrl;
  }

  public List<String> getProfessions() {
    return professions;
  }

  public void setProfessions(List<String> professions) {
    this.professions = professions;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  @Override
  public String toString() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

}
