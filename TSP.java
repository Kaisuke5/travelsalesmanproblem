import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
public class TSP {
	
	public static void main(String args[]){
		long start = System.currentTimeMillis();
		ArrayList <Integer>path=new ArrayList<Integer>();      //ルートを格納
		ArrayList <Float>x=new ArrayList<Float>();             //xの座標
		ArrayList <Float>y=new ArrayList<Float>();             //Yの座標
		HashMap <Integer,Float>costmap=new HashMap <Integer,Float>();    //path各点から最も近い点
		HashMap <Integer,Integer>pmap=new HashMap <Integer,Integer>();   //path各点から最も近い距離
		float cost=0;
		int bn,c=0,count=0,nearest=0;
		FileReader fr=null;
		BufferedReader br=null;
		String word;
		Math math;
		
		try{
			fr=new FileReader("input_3.csv");
			br=new BufferedReader(fr);
			
			
			br.readLine();
			
			while((word=br.readLine())!=null){
				String[] strAry = word.split(",");
				x.add(Float.parseFloat(strAry[0]));
				y.add(Float.parseFloat(strAry[1]));
				
				c++;		
			}
			
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				fr.close();
				br.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
		
		int n=x.size();
		
		//greedy
		/*
		int start=0;
		path.add(start);
		nearest=start;
		
		for(int i=0;i<x.size()-1;i++){
			int lp=nearest;   //looking point
			nearest=search_near(x,y,lp,path);  //search the nearest point
			path.add(nearest);
		}
		
		System.out.println(outcost(x,y,path));
		*/
		
		//ring
		/*常に閉路を作りそこから一番近い点を後からその閉路に追加する。また１点追加すごとに2-opt法を用いて修正を加える
		 計算量を減らすためpath(閉路)の各点から一番近い点はHashMapに保存しておく*/
		
		
		path.add(0);
		int min=search_near(x,y,path.get(0),path),tmp,idx=0;
		float min_cost=search_near(x,y,path.get(0),path),tmp_cost;
		
		
		
		
		for(int i=0;i<n;i++){
			if(i==0){
			}else if(i==1){
				nearest=search_near(x,y,path.get(0),path);
				path.add(nearest);
			}else if(i==2){
				min=search_near(x,y,path.get(0),path);
				tmp=search_near(x,y,path.get(1),path);
				
				tmp_cost=md(x,y,path.get(1),tmp);
				min_cost=md(x,y,path.get(0),min);
				
				if(tmp_cost<min_cost) min=tmp;
				path.add(min);
				min_cost=Float.MAX_VALUE;
			}else{
				
				for(int j=0;j<path.size();j++){
					int p=path.get(j);
					
					if(pmap.get(p)==null||!pmap.containsKey(p)){
						tmp=search_near(x,y,p,path);
						tmp_cost=md(x,y,p,tmp);
						pmap.put(p, tmp);
						costmap.put(p,tmp_cost);
						
						
					}else{
						tmp=pmap.get(p);
						tmp_cost=costmap.get(p);
						if(path.contains(tmp)){
							pmap.remove(p);
							costmap.remove(p);
							tmp=search_near(x,y,p,path);
							tmp_cost=md(x,y,p,tmp);
							pmap.put(p, tmp);
							costmap.put(p,tmp_cost);
						}
					}
					
					
					
					
					if(tmp_cost<min_cost){
						min=tmp;
						min_cost=tmp_cost;
						idx=j;
						
					}
						
				}
				
				pmap.remove(idx);
				costmap.remove(idx);
				
				insert(x,y,path,idx,min);
				System.out.println(path.size());
				
				//2-opt
				min_cost=Float.MAX_VALUE;
				if(path.size()>4){
					int ps=path.size();
					for(int k=0;k<ps-4;k++){
						for(int l=k+2;l<ps-2;l++){
							//System.out.println(path.size()+" "+l+" "+k);
							float d=md(x,y,path.get(k),path.get(k+1))+md(x,y,path.get(l),path.get(l+1));
							float d2=md(x,y,path.get(k),path.get(l))+md(x,y,path.get(k+1),path.get(l+1));
							if(d>d2){
								//System.out.println(path);
								swap(path,k+1,l);
								
								Collections.reverse(path.subList(k+2, l));
								//System.out.println(path);
								
							}
						}
					}
				}
				
			}
			
			
		}
		
		
		
			
		System.out.println("index");
		for(int a:path)System.out.println(a);
		
		System.out.println(outcost(x,y,path));
		long stop = System.currentTimeMillis();
		System.out.println(stop-start+"s");
	
	}
	public static float md(ArrayList <Float>x,ArrayList <Float> y,int p1,int p2){
		double d=Math.pow((x.get(p1)-x.get(p2)), 2)+Math.pow((y.get(p1)-y.get(p2)),2);
		d=Math.sqrt(d);
		return (float)d;
	}
	
	//点numから最も近い点を返す。ただしpathに入ってる点はのぞく
	public static int search_near(ArrayList <Float> x,ArrayList <Float> y,int num,ArrayList <Integer>path/*,int reject*/){  //search the nearest node not connected
		int len=x.size();
		double mincost=Double.MAX_VALUE,tmp=0;
		int min=path.get(0);
		for(int i=0;i<len;i++){
			if(path.contains(i)||i==num) continue;
			tmp=md(x,y,num,i);
			if(tmp<mincost){
				mincost=tmp;
				min=i;
			}
		}
		return min;
	}
	public static float outcost(ArrayList <Float>x,ArrayList <Float> y,ArrayList<Integer> path){
		float cost=0;
		for(int i=0;i<path.size()-1;i++){
			cost+=md(x,y,path.get(i),path.get(i+1));
		}
		
		return cost+md(x,y,path.get(0),path.get(path.size()-1));
	}
	public static void swap(ArrayList<Integer> a,int i,int j){
		int tmp;
		tmp=a.get(i);
		a.set(i,a.get(j));
		a.set(j, tmp);
	}

	public static void insert(ArrayList <Float> x,ArrayList <Float> y,ArrayList<Integer>path,int idx,int num){
		float d1,d2;
		
		if(idx==0){
			d1=md(x,y,num,path.get(path.size()-1));
			d2=md(x,y,num,path.get(idx+1));				
		}
		else if(idx>path.size()-2){
			d1=md(x,y,num,path.get(idx-1));			
			d2=md(x,y,num,path.get(0));			
			
		}else{
			d1=md(x,y,num,path.get(idx-1));
			d2=md(x,y,num,path.get(idx+1));
			
		}
		if(d1<d2) path.add(idx,num);
		else path.add(idx+1,num);
		
	}
		
	
	
}
