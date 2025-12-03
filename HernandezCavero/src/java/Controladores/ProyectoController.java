package Controladores;

import Entidades.Proyecto;
import Controladores.util.JsfUtil;
import Controladores.util.PaginationHelper;
import Entidades.Cad;
import Entidades.Crs;
import Entidades.Inspectoria;
import Entidades.Instrumento;
import Entidades.Odsprincipal;
import Entidades.Pais;
import Entidades.Sociolocal;
import Entidades.Status;
import Entidades.Tecnico;
import Repositorios.CadFacade;
import Repositorios.CrsFacade;
import Repositorios.InspectoriaFacade;
import Repositorios.InstrumentoFacade;
import Repositorios.OdsprincipalFacade;
import Repositorios.PaisFacade;
import Repositorios.ProyectoFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@Named("proyectoController")
@SessionScoped
public class ProyectoController implements Serializable {

    private Proyecto current;
    private DataModel items = null;
    @EJB
    private Repositorios.ProyectoFacade ejbFacade;
    @EJB
    private Repositorios.InspectoriaFacade inspectoriaFacade;
    @EJB
    private Repositorios.InstrumentoFacade instrumentoFacade;
    @EJB
    private Repositorios.PaisFacade paisFacade;
    @EJB
    private Repositorios.OdsprincipalFacade odsFacade;
    @EJB
    private Repositorios.CrsFacade crsFacade;
    @EJB
    private Repositorios.CadFacade cadFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public ProyectoController() {
    }

    public Proyecto getSelected() {
        if (current == null) {
            current = new Proyecto();
            selectedItemIndex = -1;
        }
        return current;
    }

    private ProyectoFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Proyecto) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Proyecto();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ProyectoCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Proyecto) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ProyectoUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Proyecto) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ProyectoDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Proyecto getProyecto(java.lang.Integer id) {
        return ejbFacade.find(id);
    }
    
    // Campos para filtrado por técnico
    private Tecnico tecnicoFiltro;
    private List<Proyecto> listaProyectosPorTecnico;
    
    public List<Proyecto> getListaProyectosPorTecnico() {
        if (listaProyectosPorTecnico == null) {
            listaProyectosPorTecnico = new ArrayList<>();
        }
        return listaProyectosPorTecnico;
    }
    
    // Métodos para filtrado por técnico
    public Tecnico getTecnicoFiltro() {
        return tecnicoFiltro;
    }
    
    public void setTecnicoFiltro(Tecnico tecnicoFiltro) {
        this.tecnicoFiltro = tecnicoFiltro;
    }
    
    public void setListaProyectosPorTecnico(List<Proyecto> listaProyectosPorTecnico) {
        this.listaProyectosPorTecnico = listaProyectosPorTecnico;
    }
    
    public void loadProyectosPorTecnico() {
        try {
            if (listaProyectosPorTecnico == null) {
                listaProyectosPorTecnico = new ArrayList<>();
            }
            
            if (tecnicoFiltro != null) {
                List<Proyecto> todosLosProyectos = ejbFacade.findAll();
                listaProyectosPorTecnico = new ArrayList<>();
                
                for (Proyecto proyecto : todosLosProyectos) {
                    // Verificar si el técnico es tec_for o tec_seg
                    if ((proyecto.getTecFor() != null && proyecto.getTecFor().getCodTecnico().equals(tecnicoFiltro.getCodTecnico())) ||
                        (proyecto.getTecSeg() != null && proyecto.getTecSeg().getCodTecnico().equals(tecnicoFiltro.getCodTecnico()))) {
                        listaProyectosPorTecnico.add(proyecto);
                    }
                }
                
                if (listaProyectosPorTecnico.isEmpty()) {
                    JsfUtil.addInfoMessage("El técnico seleccionado no tiene proyectos asignados.");
                }
            } else {
                listaProyectosPorTecnico = new ArrayList<>();
                JsfUtil.addErrorMessage("Por favor, seleccione un técnico.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsfUtil.addErrorMessage("Error al cargar los proyectos: " + e.getMessage());
            listaProyectosPorTecnico = new ArrayList<>();
        }
    }
    
    private Sociolocal socioLocalFiltro;
    private Inspectoria inspectoriaFiltro;
    private Status statusFiltro;
    private List<Proyecto> listaProyectosFiltrados;
    private List<Inspectoria> inspectoriasPorSocioLocal;
    
    public Sociolocal getSocioLocalFiltro() {
        return socioLocalFiltro;
    }
    
    public void setSocioLocalFiltro(Sociolocal socioLocalFiltro) {
        this.socioLocalFiltro = socioLocalFiltro;
    }
    
    public Inspectoria getInspectoriaFiltro() {
        return inspectoriaFiltro;
    }
    
    public void setInspectoriaFiltro(Inspectoria inspectoriaFiltro) {
        this.inspectoriaFiltro = inspectoriaFiltro;
    }
    
    public Status getStatusFiltro() {
        return statusFiltro;
    }
    
    public void setStatusFiltro(Status statusFiltro) {
        this.statusFiltro = statusFiltro;
    }
    
    public List<Proyecto> getListaProyectosFiltrados() {
        if (listaProyectosFiltrados == null) {
            listaProyectosFiltrados = new ArrayList<>();
        }
        return listaProyectosFiltrados;
    }
    
    public void setListaProyectosFiltrados(List<Proyecto> listaProyectosFiltrados) {
        this.listaProyectosFiltrados = listaProyectosFiltrados;
    }
    
    public List<Inspectoria> getInspectoriasPorSocioLocal() {
        if (inspectoriasPorSocioLocal == null) {
            inspectoriasPorSocioLocal = new ArrayList<>();
        }
        return inspectoriasPorSocioLocal;
    }
    
    public void setInspectoriasPorSocioLocal(List<Inspectoria> inspectoriasPorSocioLocal) {
        this.inspectoriasPorSocioLocal = inspectoriasPorSocioLocal;
    }
    
    // Cuando cambia el socio local, cargar sus inspectorías 
    public void onSocioLocalChange() {
        inspectoriaFiltro = null;
        inspectoriasPorSocioLocal = new ArrayList<>();
        listaProyectosFiltrados = new ArrayList<>();
        
        if (socioLocalFiltro != null) {
            
            inspectoriasPorSocioLocal = inspectoriaFacade.findBySocioLocal(socioLocalFiltro.getCodSocio());
        }
    }
    
    // Generar SelectItems para las inspectorías filtradas
    public SelectItem[] getInspectoriasSelectItems() {
        List<Inspectoria> lista = getInspectoriasPorSocioLocal();
        SelectItem[] items = new SelectItem[lista.size() + 1];
        items[0] = new SelectItem(null, "-- Seleccione Inspectoría --");
        int i = 1;
        for (Inspectoria insp : lista) {
            String label = insp.getCodInspectoria() + " - " + 
                          (insp.getNomInspectoria() != null ? insp.getNomInspectoria() : "");
            items[i++] = new SelectItem(insp, label);
        }
        return items;
    }
    
    // Buscar proyectos con los filtros aplicados 
    public void buscarProyectosFiltrados() {
        try {
            listaProyectosFiltrados = new ArrayList<>();
            
            if (inspectoriaFiltro == null && statusFiltro == null) {
                JsfUtil.addErrorMessage("Por favor, seleccione al menos una inspectoría o un status.");
                return;
            }
            
            // Usar consultas JPQL del Facade según los filtros seleccionados
            if (inspectoriaFiltro != null && statusFiltro != null) {
                // Ambos filtros: usar consulta combinada
                listaProyectosFiltrados = ejbFacade.findByInspectoriaAndStatus(
                        inspectoriaFiltro, statusFiltro.getCodStatus());
            } else if (inspectoriaFiltro != null) {
                // Solo inspectoría
                listaProyectosFiltrados = ejbFacade.findByInspectoria(inspectoriaFiltro);
            } else {
                // Solo status
                listaProyectosFiltrados = ejbFacade.findByStatus(statusFiltro.getCodStatus());
            }
            
            if (listaProyectosFiltrados.isEmpty()) {
                JsfUtil.addInfoMessage("No se encontraron proyectos con los filtros seleccionados.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsfUtil.addErrorMessage("Error al buscar proyectos: " + e.getMessage());
            listaProyectosFiltrados = new ArrayList<>();
        }
    }
    
    
    
    private Integer anyoNuevo;
    private Instrumento instrumentoNuevo;
    private Pais paisNuevo;
    private List<Odsprincipal> odsSeleccionados;
    private List<Crs> crsSeleccionados;
    
    public Integer getAnyoNuevo() {
        return anyoNuevo;
    }
    
    public void setAnyoNuevo(Integer anyoNuevo) {
        this.anyoNuevo = anyoNuevo;
    }
    
    public Instrumento getInstrumentoNuevo() {
        return instrumentoNuevo;
    }
    
    public void setInstrumentoNuevo(Instrumento instrumentoNuevo) {
        this.instrumentoNuevo = instrumentoNuevo;
    }
    
    public Pais getPaisNuevo() {
        return paisNuevo;
    }
    
    public void setPaisNuevo(Pais paisNuevo) {
        this.paisNuevo = paisNuevo;
    }
    
    public List<Odsprincipal> getOdsSeleccionados() {
        if (odsSeleccionados == null) {
            odsSeleccionados = new ArrayList<>();
        }
        return odsSeleccionados;
    }
    
    public void setOdsSeleccionados(List<Odsprincipal> odsSeleccionados) {
        this.odsSeleccionados = odsSeleccionados;
    }
    
    public List<Crs> getCrsSeleccionados() {
        if (crsSeleccionados == null) {
            crsSeleccionados = new ArrayList<>();
        }
        return crsSeleccionados;
    }
    
    public void setCrsSeleccionados(List<Crs> crsSeleccionados) {
        this.crsSeleccionados = crsSeleccionados;
    }
    
    // SelectItems para los combos
    public SelectItem[] getInstrumentosSelectItems() {
        List<Instrumento> lista = instrumentoFacade.findAll();
        SelectItem[] items = new SelectItem[lista.size() + 1];
        items[0] = new SelectItem(null, "-- Seleccione Instrumento --");
        int i = 1;
        for (Instrumento inst : lista) {
            String label = inst.getLt() + " - " + (inst.getDescripcion() != null ? inst.getDescripcion() : "");
            items[i++] = new SelectItem(inst, label);
        }
        return items;
    }
    
    public SelectItem[] getPaisesSelectItems() {
        List<Pais> lista = paisFacade.findAll();
        SelectItem[] items = new SelectItem[lista.size() + 1];
        items[0] = new SelectItem(null, "-- Seleccione País --");
        int i = 1;
        for (Pais pais : lista) {
            items[i++] = new SelectItem(pais, pais.getNomPais());
        }
        return items;
    }
    
    public SelectItem[] getOdsSelectItems() {
        List<Odsprincipal> lista = odsFacade.findAll();
        SelectItem[] items = new SelectItem[lista.size()];
        int i = 0;
        for (Odsprincipal ods : lista) {
            String label = ods.getCodOds() + " - " + (ods.getNomOds() != null ? ods.getNomOds() : "");
            items[i++] = new SelectItem(ods, label);
        }
        return items;
    }
    
    public SelectItem[] getCrsSelectItems() {
        List<Crs> lista = crsFacade.findAll();
        SelectItem[] items = new SelectItem[lista.size()];
        int i = 0;
        for (Crs crs : lista) {
            String label = crs.getCodCrs() + " - " + (crs.getNomCrs() != null ? crs.getNomCrs() : "");
            items[i++] = new SelectItem(crs, label);
        }
        return items;
    }
    
    // Genera el código concatenando año/instrumento/país
    private String generarCodigo() {
        StringBuilder sb = new StringBuilder();
        if (anyoNuevo != null) {
            sb.append(anyoNuevo);
        }
        sb.append("/");
        if (instrumentoNuevo != null) {
            sb.append(instrumentoNuevo.getLt());
        }
        sb.append("/");
        if (paisNuevo != null) {
            sb.append(paisNuevo.getCodPais());
        }
        return sb.toString();
    }
    
    // Método para obtener el nombre del CAD dado su código
    public String getNombreCAD(Integer codCad) {
        if (codCad == null) {
            return "";
        }
        Cad cad = cadFacade.find(codCad);
        return cad != null ? cad.getNomCad() : "";
    }
    
    // Lista completa de todos los proyectos para Ejercicio3
    public List<Proyecto> getTodosLosProyectos() {
        return ejbFacade.findAll();
    }
    
    public String insertarProyecto() {
        try {
            // Asegurar que current no sea null
            if (current == null) {
                current = new Proyecto();
            }
            
            String codigoGenerado = generarCodigo();
            
            current.setCodigo(codigoGenerado);
            current.setAnyo(anyoNuevo);
            current.setInstrumento(instrumentoNuevo != null ? instrumentoNuevo.getLt() : null);
            current.setPais(paisNuevo != null ? paisNuevo.getCodPais() : null);
            current.setOdsprincipalList(odsSeleccionados);
            current.setCrsList(crsSeleccionados);
            
            ejbFacade.create(current);
            
            JsfUtil.addSuccessMessage("Proyecto creado correctamente con código: " + codigoGenerado);
            
            current = new Proyecto();
            anyoNuevo = null;
            instrumentoNuevo = null;
            paisNuevo = null;
            odsSeleccionados = null;
            crsSeleccionados = null;
            
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage();
            if (errorMsg == null && e.getCause() != null) {
                errorMsg = e.getCause().getMessage();
            }
            if (errorMsg == null) {
                errorMsg = e.getClass().getName();
            }
            JsfUtil.addErrorMessage("Error al crear el proyecto: " + errorMsg);
            return null;
        }
    }
      
    @FacesConverter(forClass = Proyecto.class)
    public static class ProyectoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ProyectoController controller = (ProyectoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "proyectoController");
            return controller.getProyecto(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Proyecto) {
                Proyecto o = (Proyecto) object;
                return getStringKey(o.getCodProyecto());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Proyecto.class.getName());
            }
        }

    }

}
