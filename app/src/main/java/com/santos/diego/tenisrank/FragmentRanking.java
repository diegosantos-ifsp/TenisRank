package com.santos.diego.tenisrank;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentRanking#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRanking extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    SharedPreferences pref = null;

    private String nome = null;
    private String email = null;
    private Integer idUsuario = null;
    private Integer idTenista = null;
    private Integer idCoordenador = null;
    private Integer idCategoria = null;


    private Integer posicaoUsuario=-1;
    private ArrayList<Categoria> categorias = null; //armazena todas as categorias existentes
    private ArrayList<String> nomesCategorias = null; //usado para armazenar os nomes das categorias no spinner

    private ArrayList<RankingHistorico> ranking = null; //armazena os dados do último ranking gerado
    private ArrayList<Tenista> tenistas = null; //armazena os dados de todos os tenistas do ranking gerado
    // private Integer idUsuario = null;
    CustomArrayRankingAdapter adapter = null;
    private TenistasRankingAsyncTask tenistasrankingAsync = null;
    private ArrayAdapter<String> adapter_spinner = null;


    private Toolbar toolbar = null;
    private Spinner spinner = null;
    private TextView textView_Data = null;
    private TextView textView_Hora = null;
    private String IP;

    private FloatingActionButton fab = null;

    //armazena os dados do próximo jogo do tenista desafiado (com o desafiador), se existir
    private Tenista desafiado = null;
    private Tenista desafiador = null;

    private ListView lv = null;

    private Boolean precisaColocarResultado=false;

    private Regra regra = null;

    public FragmentRanking() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentRanking.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentRanking newInstance(Integer param1, Integer param2, String param3, String param4, Integer param5) {
        FragmentRanking fragment = new FragmentRanking();
        Bundle args = new Bundle();
        args.putInt("idUsuario", param1);
        args.putInt("idTenista", param2);
        args.putString("Nome", param3);
        args.putString("Email", param4);
        args.putInt("idCoordenador",param5);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();

       // regra = new Regra();
        regra.setIdRegra(Integer.valueOf(pref.getString("idRegra","-1")));
        regra.setDataAlteracao(pref.getString("DataAlteracao",""));
        regra.setQtdDiasPorMesPodeDesafiar(Integer.valueOf(pref.getString("QtdDiasPorMesPodeDesafiar","-1")));
        regra.setQtdDiasPorMesRebecerDesafio(Integer.valueOf(pref.getString("QtdDiasPMesReceberDesafio","-1")));
        regra.setPosicaoMaximaQPodeDesafiar(Integer.valueOf(pref.getString("PosicaoMaximaQPodeDesafiar","-1")));
        regra.setDesafiadorQtdPosCasoVitoria(Integer.valueOf(pref.getString("DesafiadorQtdPosCasoVitoria","-1")));
        regra.setDesafiadoQtdPosCasoVitoria(Integer.valueOf(pref.getString("DesafiadoQtdPosCasoVitoria","-1")));
        regra.setDesafiadorQtdPosCasoDerrota(Integer.valueOf(pref.getString("DesafiadorQtdPosCasoDerrota","-1")));
        regra.setDesafiadoQtdPosCasoDerrota(Integer.valueOf(pref.getString("DesafiadoQtdPosCasoDerrota","-1")));
        regra.setQtdPosCaiCasoNaoDesafieMes(Integer.valueOf(pref.getString("QtdPosCaiCasoNaoDesafieMes","-1")));
        regra.setTempoWO(Integer.valueOf(pref.getString("TempoWO","-1")));
        regra.setQtdPosicoesPerdeCasoWO(Integer.valueOf(pref.getString("QtdPosicoesPerdeCasoWO","-1")));


        if (categorias!=null && !categorias.isEmpty()) {
               // adapter.setRegra(regra);
                adapter.notifyDataSetChanged();
        }
          //  RankingAsyncTask rasync = new RankingAsyncTask(0, categorias.get(spinner.getSelectedItemPosition()).getIdCategoria());
            //rasync.execute((Void) null);
    }
/*
    @Override
    public void onResume()
    {
        super.onResume();
        Log.i("RESUME","ATIVADO");
        if (categorias!=null && !categorias.isEmpty()) {
            RankingAsyncTask rasync = new RankingAsyncTask(0, categorias.get(spinner.getSelectedItemPosition()).getIdCategoria());
            rasync.execute((Void) null);
        }
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_ranking, container, false);

        spinner = (Spinner) view.findViewById(R.id.spinner_nav);
        textView_Data = (TextView) view.findViewById(R.id.textview_data);
        textView_Hora = (TextView) view.findViewById(R.id.textview_hora);
        fab = (FloatingActionButton) view.findViewById(R.id.fab_ranking);

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        IP = pref.getString("ip","0");


        Bundle args = getArguments();
        idUsuario = args.getInt("idUsuario", 0);
        idTenista = args.getInt("idTenista",0);
        nome = args.getString("Nome",null);
        email = args.getString("Email",null);
        idCoordenador = args.getInt("idCoordenador",0);




        regra = new Regra();
        regra.setIdRegra(Integer.valueOf(pref.getString("idRegra","-1")));
        regra.setDataAlteracao(pref.getString("DataAlteracao",""));
        regra.setQtdDiasPorMesPodeDesafiar(Integer.valueOf(pref.getString("QtdDiasPorMesPodeDesafiar","-1")));
        regra.setQtdDiasPorMesRebecerDesafio(Integer.valueOf(pref.getString("QtdDiasPMesReceberDesafio","-1")));
        regra.setPosicaoMaximaQPodeDesafiar(Integer.valueOf(pref.getString("PosicaoMaximaQPodeDesafiar","-1")));
        regra.setDesafiadorQtdPosCasoVitoria(Integer.valueOf(pref.getString("DesafiadorQtdPosCasoVitoria","-1")));
        regra.setDesafiadoQtdPosCasoVitoria(Integer.valueOf(pref.getString("DesafiadoQtdPosCasoVitoria","-1")));
        regra.setDesafiadorQtdPosCasoDerrota(Integer.valueOf(pref.getString("DesafiadorQtdPosCasoDerrota","-1")));
        regra.setDesafiadoQtdPosCasoDerrota(Integer.valueOf(pref.getString("DesafiadoQtdPosCasoDerrota","-1")));
        regra.setQtdPosCaiCasoNaoDesafieMes(Integer.valueOf(pref.getString("QtdPosCaiCasoNaoDesafieMes","-1")));
        regra.setTempoWO(Integer.valueOf(pref.getString("TempoWO","-1")));
        regra.setQtdPosicoesPerdeCasoWO(Integer.valueOf(pref.getString("QtdPosicoesPerdeCasoWO","-1")));

       // Log.i("RegraFRAME",regra.getDataAlteracao());


        nomesCategorias = new ArrayList<String>();
        adapter_spinner = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,nomesCategorias);
        adapter_spinner.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter_spinner);


        if (idCoordenador==0)
            fab.setVisibility(View.GONE);



        CategoriaAsyncTask catAsync = new CategoriaAsyncTask(0);

        catAsync.execute((Void) null);



        //toda a comunicação para pegar os dados do Ranking e dos Tenistas começa aqui
        //Essa função é ativada a primeira vez que é criado o spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Integer pos = spinner.getSelectedItemPosition();

                RankingAsyncTask r = new RankingAsyncTask(0, categorias.get(pos).getIdCategoria());
                r.execute((Void) null);

                idCategoria = categorias.get(pos).getIdCategoria();
                adapter.setIdCategoria(idCategoria);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        lv = (ListView) view.findViewById(R.id.listView_Ranking);

        if (idCoordenador>0)
            registerForContextMenu(lv);


        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        tenistas = new ArrayList<Tenista>();

        adapter = new CustomArrayRankingAdapter(getActivity(), tenistas);
        lv.setAdapter(adapter);
        adapter.setNome(nome);
        adapter.setEmail(email);
        adapter.setidUsuario(idUsuario);

        adapter.setRegra(regra);


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RankingAsyncTask rasync = new RankingAsyncTask(0,categorias.get(spinner.getSelectedItemPosition()).getIdCategoria());
                rasync.execute((Void) null);
                adapter.setIdCategoria(categorias.get(spinner.getSelectedItemPosition()).getIdCategoria());
                refreshLayout.setRefreshing(false);
                //  Log.i("REFRESH", "DEU CERTO");
                /*
                if (ranking!=null) {
                    tenistasrankingAsync = new TenistasRankingAsyncTask(ranking.get(0));
                    tenistasrankingAsync.execute((Void) null);
                    refreshLayout.setRefreshing(false);
                }*/
            }
        });



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AtualizaNovoRankingAsyncTask at = new AtualizaNovoRankingAsyncTask();
                at.setIdCategoria(categorias.get(spinner.getSelectedItemPosition()).getIdCategoria());
                at.execute((Void) null);



            }
        });

        return view;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.listView_Ranking) {
            //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            //menu.setHeaderTitle(Countries[info.position]);
            //String[] menuItems = getResources().getStringArray(R.arr);
            //for (int i = 0; i<menuItems.length; i++) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_listview_ranking, menu);
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        //String[] menuItems = getResources().getStringArray(R.menu.menu_listview_categorias);
        //String menuItemName = menuItems[menuItemIndex];
        //String listItemName = Countries[info.position];

        // text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));
        switch(item.getItemId()) {
            case R.id.menu_ranking_modificar:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setTitle("Alteração da posição do jogador");
                builder1.setMessage("Insira a nova posição: \n\n");
                builder1.setCancelable(true);
                final EditText input = new EditText(getActivity());
                input.setText("1");
                input.setSelection(input.getText().length());
                builder1.setView(input);

                builder1.setPositiveButton(
                        "Alterar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                                AtualizaNovoRankingAsyncTask atualiza = new AtualizaNovoRankingAsyncTask();

                                atualiza.setMudaPosicao(true);
                                atualiza.setIdCategoria(categorias.get(spinner.getSelectedItemPosition()).getIdCategoria());
                                atualiza.setIdRanking(ranking.get(0).getIdRanking());
                                atualiza.setIdTenista(tenistas.get(info.position).getIdTenista());
                                atualiza.setNovaPos(Integer.valueOf(input.getText().toString()));

                                atualiza.execute((Void) null);
                                //CategoriaAsyncTask catAsync = new CategoriaAsyncTask();
                                //catAsync.execute((Void) null);


                            }
                        });

                builder1.setNegativeButton(
                        "Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    private class AtualizaNovoRankingAsyncTask extends AsyncTask<Void, Void, Boolean>
    {

        private Integer idCategoria = 0;
        private ProgressDialog progressDialog=null;

        private Integer novaPos=-1;
        private Integer idTenista=0;

        public Integer getIdRanking() {
            return idRanking;
        }

        public void setIdRanking(Integer idRanking) {
            this.idRanking = idRanking;
        }

        public Integer getNovaPos() {
            return novaPos;
        }

        public void setNovaPos(Integer novaPos) {
            this.novaPos = novaPos;
        }

        public Integer getIdTenista() {
            return idTenista;
        }

        public void setIdTenista(Integer idTenista) {
            this.idTenista = idTenista;
        }

        private Integer idRanking = 0;


        public Boolean getMudaPosicao() {
            return mudaPosicao;
        }

        public void setMudaPosicao(Boolean mudaPosicao) {
            this.mudaPosicao = mudaPosicao;
        }

        private Boolean mudaPosicao = false;

        public Integer getIdCategoria() {
            return idCategoria;
        }

        public void setIdCategoria(Integer idCategoria) {
            this.idCategoria = idCategoria;
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            DatabaseJson json = new DatabaseJson();

            json.setIP(IP);


            if (mudaPosicao)
            {
              json.modificaPosicaoJogador(this.idTenista,this.idRanking,this.idCategoria,this.novaPos);
            }
            else {
                if (idCategoria > 0) {
                    json.atualizaRanking(idCategoria);
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Aguarde...");
            progressDialog.setCancelable(false);
            progressDialog.show();


        }

        @Override
        protected void onPostExecute(Boolean e) {
            super.onPostExecute(e);
            RankingAsyncTask rasync = new RankingAsyncTask(0,categorias.get(spinner.getSelectedItemPosition()).getIdCategoria());
            rasync.execute((Void) null);
            adapter.setIdCategoria(categorias.get(spinner.getSelectedItemPosition()).getIdCategoria());

            progressDialog.cancel();
        }
    }


    //faz a consulta na tabela de ranking e retorna o ranking atual (último registro)
    private class RankingAsyncTask extends AsyncTask<Void, Integer,ArrayList<RankingHistorico> > {

        private ArrayList<RankingHistorico> temp = null;
        // private DatabaseJson json = null;
        private Integer tipo = null;
        private Boolean updateTenistas=true;
        private Integer idcat = null;
        private TextView textViewDesafiador;
        private TextView textViewDesafiado;
        private TextView data;
        private TextView hora;
        private LinearLayout linearLayout;
        private ProgressDialog progressDialog=null;

        RankingAsyncTask (Integer t, Integer idCat)
        {
            tipo=t;
            idcat = idCat;
            textViewDesafiador = (TextView) getActivity().findViewById(R.id.textView_Desafiador);
            textViewDesafiado = (TextView) getActivity().findViewById(R.id.textView_Desafiado);
            data = (TextView) getActivity().findViewById(R.id.textView_Proxima_Data);
            hora = (TextView) getActivity().findViewById(R.id.textView_Proxima_Hora);
            linearLayout = (LinearLayout) getActivity().findViewById(R.id.layoutStatusBar);

        } //se tipo = 1 ler o último registro adicionado


        public Boolean getUpdateTenistas() {
            return updateTenistas;
        }

        public void setUpdateTenistas(Boolean updateTenistas) {
            this.updateTenistas = updateTenistas;
        }





        @Override
        protected ArrayList<RankingHistorico> doInBackground(Void... values) {


            //  if (ranking!=null || ranking==null) {
            publishProgress(10);
            //Thread.sleep(1000);
            DatabaseJson json = new DatabaseJson();

            json.setIP(IP);
            // publishProgress(30);
            //Thread.sleep(1000);

            temp = json.getRanking(idcat);



            //atualiza os dados do próximo jogo do usuário atual

            if (idTenista!=0) {
                final ArrayList<Desafio> des;


                des = json.getJogosByTenista(0, idTenista, 1);



                if (des!=null) {


                    //  Log.i("TENISTA_ID",idTenista.toString());
                    //Log.i("TENISTA",des.get(0).getData());
                    desafiado = des.get(0).getTenistaDesafiado();
                    desafiador = des.get(0).getTenistaDesafiador();


                    //verifica se existe jogo anterior sem resultado e obriga o usuário a
                    //digitar o resultado
                    String tempData = des.get(0).getData();
                    String tempHora = des.get(0).getHora();

                    String tempDataHora = tempData + " " + tempHora;

                    Date dataJogo = null;

                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    try {
                        dataJogo = inputFormat.parse(tempDataHora);

                        Date dataAtual = new Date();

                        if (dataJogo.compareTo(dataAtual)<0)
                        {
                            precisaColocarResultado = true;

                        }
                        else
                            precisaColocarResultado = false;

                    }catch(java.text.ParseException e)
                    {
                        precisaColocarResultado = false;
                    }

                    //se existir jogo a ser jogado, este será mostrado na tela de Ranking

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (!precisaColocarResultado) {

                                    String strDesafiado;
                                    String strDesafiador;

                                    strDesafiado = desafiado.getUsuario().getNome();
                                    strDesafiador = desafiador.getUsuario().getNome();

                                    int indexDesafiado = strDesafiado.indexOf(" ");
                                    if (indexDesafiado > 0)
                                        strDesafiado = strDesafiado.substring(0, indexDesafiado);

                                    int indexDesafiador = strDesafiador.indexOf(" ");

                                    if (indexDesafiador > 0)
                                        strDesafiador = strDesafiador.substring(0, indexDesafiador);


                                    textViewDesafiador.setText(strDesafiador);
                                    textViewDesafiado.setText(strDesafiado);
                                    String tempData = des.get(0).getData();

                                    //mostra o próximo jogo do usuário
                                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    try {
                                        Date date = inputFormat.parse(des.get(0).getData());
                                        tempData = outputFormat.format(date);
                                        data.setText(tempData);
                                        hora.setText(des.get(0).getHora());
                                        linearLayout.setVisibility(View.VISIBLE);

                                    } catch (java.text.ParseException e) {
                                        linearLayout.setVisibility(View.GONE);

                                    }

                                }
                                else //se precisa colocar resultado
                                {
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                                    builder1.setTitle("Envio de Resultado");
                                    builder1.setMessage("Você jogou uma partida de tênis e não enviou o resultado final.\n " +
                                            "\nObs: Você só conseguirá marcar novos desafios caso o resultado final seja enviado." +
                                            "\n\nDeseja enviar agora?");
                                    builder1.setCancelable(true);
                                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                   // final int idTenistaDesafiado = pref.getInt("idTenistaDesafiado",0);

                                    builder1.setPositiveButton(
                                            "Sim",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                  //  dialog.cancel();
                                                    //CustomDialogMarcarResultado marcarResultado = CustomDialogMarcarResultado.newInstance(2,3,4,"","");
                                                    //FragmentManager fragmentManager = getFragmentManager();
                                                    CustomDialogMarcarResultado marcarResultado = new CustomDialogMarcarResultado();
                                                    FragmentManager fragMarcarResultado = getActivity().getSupportFragmentManager();
                                                    //marcarResultado.setIdTenistaDesafiador(desafiador.getIdTenista());
                                                    //marcarResultado.setIdTenistaDesafiado(desafiado.getIdTenista());
                                                    marcarResultado.setDesafio(des.get(0));
                                                    //marcarResultado.setIdDesafio(des.get(0).getIdDesafio());
                                                    marcarResultado.show(fragMarcarResultado,"marcarResultadoDialog");
                                                /*
                                                    if (idTenistaDesafiado>0) {
                                                        CustomDialogDesafioMarcado customDialogDesafio = CustomDialogDesafioMarcado.newInstance(idUsuario,idTenista,idTenistaDesafiado,nome,email);
                                                        FragmentManager fragmentManager = getSupportFragmentManager();
                                                        //fragmentManager.beginTransaction().replace(R.id.content_frame,fragment).commit();


                                                        // FragmentManager fm = getSupportFragmentManager();
                                                        //CustomDialogDesafioMarcado dialogcustom = new CustomDialogDesafioMarcado();
                                                        customDialogDesafio.show(fragmentManager, "customdialog");
                                                    }
                                                    */

                                                }
                                            });

                                    builder1.setNegativeButton(
                                            "Não",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });

                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();

                                }
                            }
                        });

                        //  Log.i("TENISTADESAFIADO", desafiado.getUsuario().getNome());
                        //Log.i("TENISTADESAFIADOR",desafiador.getUsuario().getNome());
                        // Log.i("TENISTADESAFIADOR", desafiador.getUsuario().getNome());

                }
            }

            //temp = json.getTenistasByRankingID(2);

            //publishProgress(60);
            //Thread.sleep(1000);

            return temp;

            //}


            //return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Aguarde...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<RankingHistorico> rank) {
            super.onPostExecute(rank);
            ranking = rank;
            if (rank!=null && !rank.isEmpty()) {
                //     adapter.setItem(tenistas);
                final RankingHistorico rhistorico = ranking.get(0);

                // toolbar.setSubtitle("Gerado em: "+rhistorico.getData()+" as " + rhistorico.getHora());
                String tempData = null;
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = inputFormat.parse(rhistorico.getData());
                    tempData = outputFormat.format(date);

                }catch (java.text.ParseException e)
                {

                }

                textView_Data.setText(tempData);
                textView_Hora.setText(rhistorico.getHora());

                if (updateTenistas) {
                    tenistasrankingAsync = new TenistasRankingAsyncTask(rhistorico);


                    tenistasrankingAsync.execute((Void) null);
                }

                //   adapter.notifyDataSetChanged();

            }
            else
            {
                if (tenistas!=null) {
                    tenistas.clear();
                    adapter.setItem(tenistas);
                    adapter.notifyDataSetChanged();
                }

                textView_Data.setText("");
                textView_Hora.setText("");

            }
            progressDialog.cancel();

        }

    }






    private class TenistasRankingAsyncTask extends AsyncTask<Void, Integer,ArrayList<Tenista> > {

        private ArrayList<Tenista> temp = null;
        //private ArrayList<Desafio> tempDesafio = null;
        // private DatabaseJson json = null;
        private RankingHistorico rank_temp;
        private Boolean podeMarcarJogo=false;

        TenistasRankingAsyncTask (RankingHistorico historico)
        {
            rank_temp = historico;
        }

        @Override
        protected ArrayList<Tenista> doInBackground(Void... values) {


            //  if (ranking!=null || ranking==null) {
//                publishProgress(10);
            //Thread.sleep(1000);
            if (idTenista<=0)
                return null;

            DatabaseJson json = new DatabaseJson();
            json.setIP(IP);

            // publishProgress(30);
            //Thread.sleep(1000);
            //              Log.i("RANKING","DEPOIS");
            //ArrayList<Usuario> users = json.getUsersByEmail(mEmail);
            temp = json.getTenistasByRankingID(rank_temp.getIdRanking());

            //atualiza a posição atual no ranking do usuário
            int pos=0;



            if (temp!=null) {
                for (int x = 0; x < temp.size(); x++)
                    if (temp.get(x).getUsuario().getEmail().compareToIgnoreCase(email) == 0) {
                        posicaoUsuario = temp.get(x).getPosicaoAtualRanking();


                        pos = x;

                        //                        Log.i("POSICAO", posicaoUsuario.toString());
                        break;

                    }


                //verifica (de acordo com as regras) os jogadores acima do jogador atual que possui jogos marcados
                //e, portanto, não poderão aceitar desafios


                ArrayList<Desafio> tempDesafio = null;
                ArrayList<Desafio> tempDesafio2 = null;

                if (temp!=null && posicaoUsuario!=-1) {

                    //substituir o 3 pela quantidade de jogadores que poderão ser desafiados
                    Integer posicaoMaxima = pos-regra.getPosicaoMaximaQPodeDesafiar();

                    if (posicaoMaxima<0)
                        posicaoMaxima=0;
                    for (int x = pos; x >= posicaoMaxima; x--) {


                        tempDesafio = json.getJogosByTenista(0, temp.get(x).getIdTenista(), 0);

                        if (tempDesafio != null) {
                            temp.get(x).setTemJogoMarcado(true);
                        } else
                            temp.get(x).setTemJogoMarcado(false);


                        if (x!=pos) {
                            Integer qtdDesafiosRec = json.getQtdDesafios(2, temp.get(x).getIdTenista(), idCategoria);

                            if (qtdDesafiosRec!=null)

                                if (qtdDesafiosRec >= regra.getQtdDiasPorMesRebecerDesafio())
                                    temp.get(x).setTemJogoMarcado(true);


                        }
                        //procura por jogos já jogados mas que não foram para o ranking ainda
                        //caso existam, o usuário não poderá marcar novos desafios e nem receber
                        //novos desafios só poderão ser agendados caso o jogo do usuário já tenha
                        //sido atualizado no ranking
                        tempDesafio2 = json.getJogosByTenista(1,temp.get(x).getIdTenista(),0);

                        if (tempDesafio2!=null)
                        {
                            if (tempDesafio2.get(0).getEstaNoRanking()==0)
                                temp.get(x).setTemJogoMarcado(true);
                        }


                       // adapter.setUsuario_pode_marcar_jogo(!temp.get(x).getTemJogoMarcado());


                        //usuario atual
                        if (x == pos) {



                            Integer qtdDesafios = json.getQtdDesafios(1,temp.get(x).getIdTenista(),idCategoria);

                            if (qtdDesafios!=null)
                                if (qtdDesafios>=regra.getQtdDiasPorMesPodeDesafiar())
                                    temp.get(x).setTemJogoMarcado(true);



                            podeMarcarJogo=!temp.get(x).getTemJogoMarcado();
                            //adapter.setUsuario_pode_marcar_jogo(!temp.get(x).getTemJogoMarcado());
                            //adapter.notifyDataSetChanged();
                        }

                        //verifica se os jogadores já jogaram todos os jogos possíveis de acordo com
                        //as regras: Exemplo: Um jogador não poderá desafiar mais do que 4 jogadores
                        //e não poderá receber mais do que 4 desafios em um mês


                    }
                }
            }
            //publishProgress(60);
            //Thread.sleep(1000);

            return temp;

            //}


            //return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(ArrayList<Tenista> tenistasTemp) {
            super.onPostExecute(tenistasTemp);
            if (tenistasTemp!=null) {
                if (tenistas!=null)
                    tenistas.clear();
                tenistas = tenistasTemp;
                adapter.setPosicaoUsuario(posicaoUsuario);
                adapter.setIdTenista(idTenista);
                adapter.setUsuario_pode_marcar_jogo(podeMarcarJogo);
                adapter.setItem(tenistasTemp);

                adapter.notifyDataSetChanged();

            }
            else
            {
                if (tenistas!=null) {
                    tenistas.clear();
                    tenistas = null;
                    adapter.notifyDataSetChanged();
                }
            }
        }

    }



    //popula a variável Categorias com todas as categorias existentes e atualizar o spinner
    private class CategoriaAsyncTask extends AsyncTask<Void, Integer,ArrayList<Categoria> > {

        private ArrayList<Categoria> temp = null;
        // private DatabaseJson json = null;
        private Integer idCategoria=null;

        CategoriaAsyncTask (Integer i)
        {
            idCategoria=i;
        }

        @Override
        protected ArrayList<Categoria> doInBackground(Void... values) {


            //  if (ranking!=null || ranking==null) {
            publishProgress(10);
            //Thread.sleep(1000);
            DatabaseJson json = new DatabaseJson();

             json.setIP(IP);
            // publishProgress(30);
            //Thread.sleep(1000);
        //    Log.i("RANKING","DEPOIS");
            //ArrayList<Usuario> users = json.getUsersByEmail(mEmail);
            temp = json.getCategorias();

            //publishProgress(60);
            //Thread.sleep(1000);

            return temp;

            //}


            //return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(ArrayList<Categoria> cat) {
            super.onPostExecute(cat);
            if (cat!=null) {
                //;
                categorias = cat;
                if(nomesCategorias!=null)
                    nomesCategorias.clear();
                for (int x=0; x<categorias.size(); x++) {
                    nomesCategorias.add(categorias.get(x).getNome());
      //              Log.i("CAT",categorias.get(x).getNome());
                }
                adapter_spinner.notifyDataSetChanged();


            }
        }

    }

}
