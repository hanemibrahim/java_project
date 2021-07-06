/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.jobsapp;

/**
 *
 * @author Administrator
 */
public class Job {
    static int id = 0;
    private String Title;
    private String Company;
    private String Location;
    private String Type;
    private String Level;
    private String YearsExp;
    private String Country;
    private String Skills;
    
    
    // setters
    public void setTitle(String Title){
        this.Title = Title;
    }
    public void setCompany(String Company){
        this.Company = Company;
    }
    public void setLocation(String Location){
        this.Location = Location;
    }
    public void setType(String Type){
        this.Type = Type;
    }
    public void setLevel(String Level){
        this.Level = Level;
    }
    public void setYearsExp(String YearsExp){
        this.YearsExp = YearsExp;
    }
    public void setCountry(String Country){
        this.Country = Country;
    }
    public void setSkills(String Skills){
        this.Skills = Skills;
    }
    
    // getters
    public String getTitle(){
        return this.Title;
    }
    public String getCompany(){
        return this.Company;
    }
    public String getLocation(){
        return this.Location;
    }
    public String getType(){
        return this.Type;
    }
    public String getLevel(){
        return this.Level;
    }
    public String getYearsExp(){
        return this.YearsExp;
    }
    public String getCountry(){
        return this.Country;
    }
    public String getSkills(){
        return this.Skills;
    }
    
    
}
