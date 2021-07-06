/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import smile.io.Read;
import smile.data.DataFrame;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.Styler;

/**
 *
 * @author Administrator
 */
@RestController
public class WebServicesController {
    
    
    
    private static DataFrame JobDataFrame;
    private static List<Job> jobs = new ArrayList<Job>();
    static{
        System.out.println("resting......");
        JobDataFrame = DataframeFromCSV("jobs.csv");
        System.out.println(JobDataFrame.structure());
    }
    
    @GetMapping("/index")
    public String takeRest(){
        
        return "hello";
    }
    
    @RequestMapping("/popularAreaChart")
    @ResponseBody
    public void popularAreas_BarChart() throws IOException {
         LinkedHashMap<String,Integer> FivePopularAreas = limitMap(getOccurence(JobDataFrame,"Location"),5);
        //FivePopularAreas.forEach((k,v)->System.out.println("area: "+k+" has " + v + " occurence"));
        
        // Show step 8 in bar chart 
        popularAreasBarChart(FivePopularAreas);
        String url = "C:\\Users\\Administrator\\Desktop\\ITI\\Java and UML\\project\\JobsWebService\\src\\main\\resources\\static\\PopularAreas.jpg";

StringBuilder b = new StringBuilder();

b.append("<img src=");
b.append(url);
b.append("alt='No Image Available' width='350px' height='200px' >");  
    
    }
    
    // URL:
    // http://localhost:8080/jobs
    // http://localhost:8080/jobs.xml
    // http://localhost:8080/jobs.json
    @RequestMapping(value = "/jobs", //
            method = RequestMethod.GET)
            //produces = { MediaType.APPLICATION_JSON_VALUE,
              //      MediaType.APPLICATION_XML_VALUE })
    @ResponseBody
    public List<Job> getJobs() {
        jobs = getJobsList("jobs.csv");
        //String s =  jobs.get(0).getTitle();
        //System.out.println(s);
        //String[][] summary = JobDataFrame.structure().toStrings(10);
        
        
        return jobs;
    }
    
    
   
    
    
    public static DataFrame DataframeFromCSV(String path) {
        CSVFormat format = CSVFormat.DEFAULT.withFirstRecordAsHeader ();
        //DataFrame df = null;
        try {
            JobDataFrame = Read.csv(path, CSVFormat.DEFAULT.withDelimiter(',')
                .withHeader("Title","Company","Location","Type","Level","YearsExp","Country","Skills")
                .withSkipHeaderRecord(true));
            //System.out.println(df.summary ());
            //df = df.select ("Name", "Pclass", "Age", "Sex", "Survived");
            //System.out.println(df.summary ());
             //System.out.println (df.structure ());
       // System.out.println (df.summary());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace ();
        }
        // System.out.println (df.summary ());
        return JobDataFrame;
    }
    
    public static List<Job> getJobsList(String filename){
        String line=null;
        String[] features=null;
        
        Job.id += 1;
        //List<Job> jobs = new ArrayList<Job>();
        File f = new File(filename);
        Scanner sc = null;
        try {
            
            sc = new Scanner(f);
            line = sc.nextLine();
            while(sc.hasNextLine()){
                Job job = new Job ();
                line = sc.nextLine();
                features = line.split(",");
                
                job.setTitle(features[0]);
                job.setCompany(features[1]);
                job.setLocation(features[2]);
                job.setType(features[3]);
                job.setLevel(features[4]);
                job.setYearsExp(features[5]);  //convert 1 to true, 0 to false
                job.setCountry(features[6]);
                job.setSkills(features[7]);
                jobs.add(job);
                
                
            }
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            sc.close();
        }  //closes the scanner  }
        return jobs;
 
        }
    
    public LinkedHashMap<String,Integer> JobsPerCompany(List<Job> jobs){

        Map<String,List<Job>> groupjobsByCompany=jobs.stream().collect(Collectors.groupingBy(Job::getCompany)); //map jobs in same company
        
        /*
        groupjobsByCompany.forEach((comp,job)->{
                                        System.out.print("\n jobs in "+comp+" are: \n");                             
                                        groupjobsByCompany.get(comp).forEach((c)->System.out.println(c.getTitle()));
                                        }
                                );
        */
        LinkedHashMap<String,Integer> countJobsInCompany = new LinkedHashMap<>(); 
        for(Map.Entry<String, List<Job>> entry:groupjobsByCompany.entrySet()){
            countJobsInCompany.put(entry.getKey(), entry.getValue().size());
            
        }
        
        LinkedHashMap<String,Integer> countJobsInCompanySorted = new LinkedHashMap<>();
        countJobsInCompanySorted = sortMapDescending(countJobsInCompany);
        return countJobsInCompanySorted;
        
    }
    
    public LinkedHashMap<String,Integer> sortMapDescending(LinkedHashMap<String,Integer> unsortedmap){
        LinkedHashMap<String,Integer> sortedmap = new LinkedHashMap<>();
        
        unsortedmap.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) 
            .forEachOrdered(x -> sortedmap.put(x.getKey(), x.getValue()));
        
        
        return sortedmap;
    }
    
    
    public LinkedHashMap<String,Integer> getOccurence(DataFrame data,String col){

        LinkedHashMap<String, Integer> occurenceMap = new LinkedHashMap<String, Integer>();
        String[] titleCol= data.column(col).toStringArray();
        // get occurence of data in column
        for (String i : titleCol) {
            Integer j = occurenceMap.get(i);
            occurenceMap.put(i, (j == null) ? 1 : j + 1);
        }
        LinkedHashMap<String,Integer> sortedPopularJobs = new LinkedHashMap<>();        
        sortedPopularJobs = sortMapDescending(occurenceMap);
        //sortedPopularJobs.forEach((k,v)->System.out.println("company "+k+" has " + v + " jobs"));
        //Map<String, Integer> firstFivePopular = sortedPopularJobs.entrySet().stream().limit(5).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        
        return sortedPopularJobs;
    
    }
    
    public LinkedHashMap<String,Integer> limitMap(LinkedHashMap<String,Integer> map, int limit){
    
        LinkedHashMap<String,Integer> limitedMap = new LinkedHashMap<>();
        int f = 1;
        for(Map.Entry<String, Integer> entry:map.entrySet()){
            limitedMap.put(entry.getKey(),entry.getValue());
            f+=1;
            if(f>limit)break;
        }
    return limitedMap;
    }

    public void JobsCompanyPieChart(LinkedHashMap<String,Integer> countJobsInCompanySorted) {
        //filter to get a map of passenger class and total number of passengers in each class
        
        // Create Chart
        PieChart chart = new PieChartBuilder ().width (800).height (600).title ("Jobs count per company").build ();
        // Customize Chart
        Color[] sliceColors = new Color[]{new Color (180, 68, 50), new Color (130, 105, 120), new Color (80, 143, 160)
        , new Color (80, 130, 190), new Color (70, 50, 110)};
        chart.getStyler ().setSeriesColors (sliceColors);
        // Series
        int f = 1;
        for(Map.Entry<String, Integer> entry:countJobsInCompanySorted.entrySet()){
            chart.addSeries (entry.getKey(), entry.getValue());
            f+=1;
            if(f>5)break;
        }
        
        // Show it
        new SwingWrapper (chart).displayChart ();
        
    }
    
    public void popularJobsBarChart(LinkedHashMap<String,Integer> popularJobs) {
        

        // Create Chart
        CategoryChart chart = new CategoryChartBuilder ().width (1024).height (768).title ("popular jobs").xAxisTitle ("Jobs").yAxisTitle ("occurence").build ();
        // Customize Chart
        chart.getStyler ().setLegendPosition (Styler.LegendPosition.InsideNW);
        chart.getStyler ().setHasAnnotations (true);
  //      chart.getStyler ().setStacked (true);
        // Series
        List jobs = new ArrayList(popularJobs.keySet());
        List occurence= new ArrayList(popularJobs.values());
        chart.addSeries ("Popular jobs ", jobs, occurence);
        // Show it
        new SwingWrapper (chart).displayChart ();
    }
 
    public void popularAreasBarChart(LinkedHashMap<String,Integer> popularAreas) throws IOException {
        

        // Create Chart
        CategoryChart chart = new CategoryChartBuilder ().width (1024).height (768).title ("popular areas").xAxisTitle ("area").yAxisTitle ("occurence").build ();
        // Customize Chart
        chart.getStyler ().setLegendPosition (Styler.LegendPosition.InsideNW);
        chart.getStyler ().setHasAnnotations (true);
  //      chart.getStyler ().setStacked (true);
        // Series
        List areas = new ArrayList(popularAreas.keySet());
        List occurence= new ArrayList(popularAreas.values());
        chart.addSeries ("Popular areas ", areas, occurence);
        // Show it
         BitmapEncoder.saveBitmap(chart, "C:\\Users\\Administrator\\Desktop\\ITI\\Java and UML\\project\\JobsWebService\\src\\main\\resources\\static\\PopularAreas", BitmapFormat.JPG);
        //new SwingWrapper (chart).displayChart ();
    }
    
    public static String listAllMapDataToString(Map<String, List<Job>> map, List<Object> topJobTitles, String title1, String title2) {
        StringBuilder sb = new StringBuilder();
        String html_table_header = "<table style=\"width:100%\" border=\"1\"> "
                + "<tr>"
                + "    <th>" + title1 + "</th>"
                + "    <th>" + title2 + "</th>"
                + "  </tr>";
        sb.append(html_table_header);

        for (Object o : topJobTitles) {
            String key = (String) o;
            List<Job> value = map.get(key);
            String html_tr = "<tr>"
                    + "    <th >" + key + "</th>"
                    + "    <th >" + value.size() + "</th>"
                    + "  </tr>";
            sb.append(html_tr);
        }
        sb.append("</table>");
        String res = sb.toString();
        return res;
    }
    
}
