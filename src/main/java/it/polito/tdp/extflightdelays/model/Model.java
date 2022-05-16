package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {

	private Graph<Airport, DefaultWeightedEdge> grafo;
	private ExtFlightDelaysDAO dao;
	private Map<Integer, Airport> idMap;
	
	public Model() {
		this.dao = new ExtFlightDelaysDAO();
		this.idMap = new HashMap<>();
		dao.loadAllAirports(this.idMap);
	}

	public void creaGrafo(int x) {
		// istanzio il grafo nel metodo per partire da zero ogni volta
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		// aggiungo i vertici
		Graphs.addAllVertices(this.grafo, this.dao.getVertici(x, this.idMap));
		
		// aggiungo gli archi
		for(Rotta r : this.dao.getRotte(this.idMap)) {
			if(this.grafo.containsVertex(r.getA1()) && 
					this.grafo.containsVertex(r.getA2())) {
				DefaultWeightedEdge edge = this.grafo.getEdge(r.getA1(), r.getA2());
				if(edge == null) {
					// non c'e' ancora un arco tra a1 e a2 (neanche nel verso opposto)
					Graphs.addEdge(this.grafo, r.getA1(), r.getA2(), r.getnVoli());
				}
				else {
					double pesoVecchio = this.grafo.getEdgeWeight(edge);
					double pesoNuovo = pesoVecchio + r.getnVoli();
					this.grafo.setEdgeWeight(edge, pesoNuovo);
				}
			}

		}
		System.out.println("# vertici: " + this.grafo.vertexSet().size() + "\n");
		System.out.println("# archi: " + this.grafo.edgeSet().size() + "\n");	
	}
	
	public List<Airport> getVertici() {
		List<Airport> vertici = new ArrayList<>(this.grafo.vertexSet());
		Collections.sort(vertici);
		return vertici;
	}
	
	public List<Airport> getPercorso(Airport a1, Airport a2) {
		List<Airport> percorso = new ArrayList<>();
		BreadthFirstIterator<Airport, DefaultWeightedEdge> it = 
				new BreadthFirstIterator<>(this.grafo, a1);
		boolean trovato = false;
		
		// visito il grafo
		while(it.hasNext())	{
			Airport visitato = it.next();
			if(visitato.equals(a2)) {
				trovato = true;
			}
		}
		
		// ottengo il percorso
		if(trovato) {
			percorso.add(a2);
			Airport step = it.getParent(a2);
			while(!step.equals(a1)) {
				percorso.add(0, step);
				step = it.getParent(step);
			}
			
			percorso.add(0, a1);
			return percorso;
		}
		else {
			return null;
		}
	}
}
