/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.jobsapp;

import com.github.lwhite1.tablesaw.api.Table;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import smile.data.DataFrame;
import smile.data.vector.IntVector;

/*
import java.io.IOException;
import java.util.stream.Collectors;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.*;
*/

/**
 *
 * @author Administrator
 */
public class NewMain {

   
    public static void main(String[] args) throws IOException {
        String path = "src/main/resources/data/jobs.csv";
        DAO DA = new DAO ();
        DataFrame trainData = DA.DataframeFromCSV(path);
        DataFrame cleanedData = DA.processTrainData(trainData);
        //System.out.println (trainData.structure ());
        //System.out.println (trainData.summary());

        // Count the jobs for each company and display that in order
        System.out.println("*************** Count the jobs for each company(10 samples) ******************");
        LinkedHashMap<String,Integer> countJobsInCompanySorted = new LinkedHashMap<>();
        countJobsInCompanySorted = DA.limitMap(DA.JobsPerCompany(DA.getJobsList(path)),10);
        countJobsInCompanySorted.forEach((k,v)->System.out.println("company "+k+" has " + v + " jobs"));
        
        
        // Show step 4 in a pie chart 
        DA.JobsCompanyPieChart(countJobsInCompanySorted);
        
        // the most popular job titles
        System.out.println("*************** most FivePopularTitls ******************");
        LinkedHashMap<String,Integer> FivePopularJobs = DA.limitMap(DA.getOccurence(cleanedData,"Title"),5);
        FivePopularJobs.forEach((k,v)->System.out.println("job "+k+" has " + v + " occurence"));
        
        // Show step 6 in bar chart 
        DA.popularJobsBarChart(FivePopularJobs);
        
        //  Find out the most popular areas
        System.out.println("*************** most FivePopularAreas ******************");
        LinkedHashMap<String,Integer> FivePopularAreas = DA.limitMap(DA.getOccurence(cleanedData,"Location"),5);
        FivePopularAreas.forEach((k,v)->System.out.println("area: "+k+" has " + v + " occurence"));
        
        // Show step 8 in bar chart 
        DA.popularAreasBarChart(FivePopularAreas);

        // Print skills one by one and how many each repeated and 
        //order the output to find out the most important skills required
        //System.out.println("*************** ALL Skills occurence ******************");
        LinkedHashMap<String,Integer> Popularskills = DA.getOccurence(cleanedData,"Skills");
        //Popularskills.forEach((k,v)->System.out.println("skill: "+k+" has " + v + " occurence"));
        System.out.println("*************** most FivePopularSkills ******************");
        LinkedHashMap<String,Integer> FivePopularSkills = DA.limitMap(DA.getOccurence(cleanedData,"Skills"),5);
        FivePopularSkills.forEach((k,v)->System.out.println("skill: "+k+" has " + v + " occurence"));
        
        // Factorize the YearsExp feature and convert it to numbers in new col
        cleanedData = cleanedData.merge(IntVector.of("YearsExpNumeric", DA.encodeCategory(cleanedData, "YearsExp")));
        cleanedData = cleanedData.drop("YearsExp");
        //System.out.println(cleanedData.structure());
}
}
