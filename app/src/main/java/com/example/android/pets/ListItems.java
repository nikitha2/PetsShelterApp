package com.example.android.pets;

class ListItems {

    String petName;
    String petBreed;
    String PetGender;
    String PetWeight;

    public ListItems(String petName, String petBreed, String petGender, String petWeight) {
        this.petName = petName;
        this.petBreed = petBreed;
        PetGender = petGender;
        PetWeight = petWeight;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetBreed() {
        return petBreed;
    }

    public void setPetBreed(String petBreed) {
        this.petBreed = petBreed;
    }

    public String getPetGender() {
        return PetGender;
    }

    public void setPetGender(String petGender) {
        PetGender = petGender;
    }

    public String getPetWeight() {
        return PetWeight;
    }

    public void setPetWeight(String petWeight) {
        PetWeight = petWeight;
    }
}
