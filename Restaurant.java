package com.hello;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class Restaurant {

	public static void main(String[] ar){

		//path of the data file
        String path="C:\\data.txt";
		Map<Integer,List<Integer>> menu=getMenu(path);
		Scanner scanner=new Scanner(System.in);
		System.out.println("How much time Gordon Ramsey will spend in restaurant");
		Integer ramseyTime=scanner.nextInt();
		scanner.close();
		Set<Integer> times=menu.keySet();
		List<Integer> timeList=new ArrayList<Integer>(times);
		List<Integer> closestTimes=getClosestTimes(timeList,ramseyTime);
		calculateMaximunSatisfaction(menu,closestTimes,ramseyTime);
	}

	
	/* This method is used to calculate the maximum satisfaction from different combination of times 
	 * */
	private static void calculateMaximunSatisfaction(Map<Integer, List<Integer>> menu, List<Integer> closestTimes,Integer ramsayTime) {

		Map<Integer,List<String>> statisfactionMap=new TreeMap<Integer,List<String>>();
		List<List<Integer>>  combinations=getCombinations(menu,closestTimes,ramsayTime);
		System.out.println("************");
		System.out.println("Combinations");
		System.out.println("************");
		
		for(List<Integer> combintation:combinations){
			/*  This block of code is used to calculate the highest satisfaction
			 *  and put that in map
			 * */
			int sum=0;
			String combo="";
			for(Integer time:combintation){
				if(!combo.equals(""))
					combo=combo+"+"+time;
				else
					combo=combo+time; 
				List<Integer> satisfactions=menu.get(time);
				if(satisfactions.size()>1){
					Collections.sort(satisfactions);
					sum=sum+satisfactions.get(satisfactions.size()-1);
				}else{
					sum=sum+satisfactions.get(0); 
				}
			}
			System.out.println(combo+":"+sum);
			List<String> combinationStrings=new ArrayList<String>();
			if(statisfactionMap.get(sum)!=null){
				combinationStrings=statisfactionMap.get(sum);
				combinationStrings.add(combo);
			}else{
				combinationStrings.add(combo);	 
			}
			statisfactionMap.put(sum,combinationStrings);
		}
		for(Integer closestTime:closestTimes){
			/*  This block of code is used to put the normal time with satisfaction in the map
			 * */
			List<Integer> satisfactions=menu.get(closestTime);
			if(satisfactions.size()>1){
				Collections.sort(satisfactions);
			}
			List<String> combinationStrings=new ArrayList<String>();
			String combo=closestTime+"";
			combinationStrings.add(closestTime+"");
			Integer satisfaction=satisfactions.get(satisfactions.size()-1);
			System.out.println(combo+":"+satisfaction);
			statisfactionMap.put(satisfaction,combinationStrings);
		}
		//System.out.println(statisfactionMap);
		System.out.println("--------------------------------------");
		Set<Integer> keys=statisfactionMap.keySet();
		if(keys.size()>1){
			List<Integer> keysList=new ArrayList<Integer>(keys);
			Integer highestSatisfaction=keysList.get(keysList.size()-1);
			System.out.println("Highest satisfaction: "+highestSatisfaction);
			System.out.println("---------------------------------------------");
			System.out.println("If Gordon Ramsey wants to get highest satifacation try to have following combintaions");
			for(String combo:statisfactionMap.get(highestSatisfaction)){
				System.out.println(" "+combo);
			} 
		} 
	}

	/*  This method is used to get the combinations(times) list .
	 *  The combination must be less than or equal to given time
	 * */
	private static List<List<Integer>> getCombinations(Map<Integer, List<Integer>> menu, List<Integer> closestTimes,
			Integer ramsayTime) {
		// TODO Auto-generated method stub
		List<List<Integer>> combinations=new ArrayList<>();
		for(int i=0;i<closestTimes.size();i++){
			int sum=closestTimes.get(i);
			List<Integer> combination=new ArrayList<Integer>();
			combination.add(sum);
			for(int j=i+1;j<closestTimes.size();j++){
				int closestTime=closestTimes.get(j);
				sum=sum+closestTime;
				if(sum<=ramsayTime){
					combination.add(closestTime);
				} else{
					sum=closestTimes.get(i);
					if(combination.size()>1){
						combinations.add(combination);
						j=j-1;
					}
					combination=new ArrayList<Integer>();
					combination.add(sum);
					continue;
				}
			}
		}
		System.out.println(combinations);
		return combinations;

	}

	/*  Get the sublist of times less than or equal to given time  
	 * */
	private static List<Integer> getClosestTimes(List<Integer> times, Integer ramseyTime) {

		List<Integer> closestTimes=new ArrayList<Integer>();
		/*  If the given time is exactly there in the times list then directly 
		 *  get the sublist of times with index of given time
		 * */
		if(times.contains(ramseyTime)){
			int lastIndex=times.indexOf(ramseyTime);
			closestTimes=times.subList(0,lastIndex+1);
		}else{
			/* If the give time is not there in the times list then find out the 
			 * closest time to given time and get the sublist of times with index of closest time
			 * */
			int lower = 0;
			int higher = times.size()-1;
			int closestTime = 0;
			while (lower <= higher) {
				int mid = (lower + higher) / 2;
				closestTime = times.get(mid);
				if (ramseyTime < closestTime) {
					higher = mid - 1;
				} else if (ramseyTime > closestTime) {
					lower = mid + 1;
				}
			}
			System.out.println("closest time:"+closestTime);
			int lastIndex=times.indexOf(closestTime);
			closestTimes=times.subList(0,lastIndex+1);

		}
		System.out.println(closestTimes);
		return closestTimes;

	}

	/*  Reads the data from data file and converts that data into map for easy process
	 *  
	 * */
	private static Map<Integer,List<Integer>> getMenu(String path) {
		BufferedReader br=null;
		//Map<Time,satisfaction>
		Map<Integer,List<Integer>> menu=new TreeMap<Integer,List<Integer>>();
		try {
			FileReader reader=new FileReader(path);
			br=new BufferedReader(reader);  
			String line=null;
			Set<Integer> keys=menu.keySet();
			while((line=br.readLine())!=null){
				/* split the each line with " "(space) to get satisfaction and time separately 
				 * */
				String[] strArr=line.split(" ");
				Integer time=Integer.parseInt(strArr[1]);
				Integer satisfaction=Integer.parseInt(strArr[0]);
				List<Integer> satisfactions=new ArrayList<Integer>();
				if(keys.contains(time)){
					satisfactions=menu.get(time);
					satisfactions.add(satisfaction);
				}else{
					satisfactions.add(satisfaction);
				}
				/*  put the time as key and satisfaction as values
				 *  if same time has more than one satisfaction then satisfactions maintained as list    
				 * 
				 * */
				menu.put(time, satisfactions);

			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return menu;

	}

}
