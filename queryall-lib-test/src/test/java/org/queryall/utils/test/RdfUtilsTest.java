/**
 * 
 */
package org.queryall.utils.test;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.namespace.RegexValidatingNamespaceEntry;
import org.queryall.api.namespace.ValidatingNamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.profile.ProfileSchema;
import org.queryall.api.provider.HttpProvider;
import org.queryall.api.provider.HttpProviderSchema;
import org.queryall.api.provider.HttpSparqlProvider;
import org.queryall.api.provider.Provider;
import org.queryall.api.provider.ProviderSchema;
import org.queryall.api.provider.RdfProvider;
import org.queryall.api.provider.SparqlProvider;
import org.queryall.api.querytype.InputQueryType;
import org.queryall.api.querytype.ProcessorQueryType;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.querytype.RdfInputQueryType;
import org.queryall.api.querytype.RdfOutputQueryType;
import org.queryall.api.querytype.RegexInputQueryType;
import org.queryall.api.querytype.SparqlProcessorQueryType;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.PrefixMappingNormalisationRule;
import org.queryall.api.rdfrule.RegexNormalisationRule;
import org.queryall.api.rdfrule.SparqlAskRule;
import org.queryall.api.rdfrule.SparqlConstructRule;
import org.queryall.api.rdfrule.SparqlConstructRuleSchema;
import org.queryall.api.rdfrule.SparqlNormalisationRule;
import org.queryall.api.rdfrule.XsltNormalisationRule;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.ruletest.SparqlRuleTest;
import org.queryall.api.ruletest.StringRuleTest;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.NamespaceMatch;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.utils.RdfUtils;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfUtilsTest
{
    /**
     * There will always be cases where unexpected triples appear in annotated configurations, so
     * this is FALSE by default to match reality If you want to test a new feature is being parsed
     * correctly, you can temporarily turn this on
     */
    private static final boolean FAIL_ON_UNEXPECTED_TRIPLES = true;
    
    private Repository testRepository;
    private RepositoryConnection testRepositoryConnection;
    private ValueFactory testValueFactory;
    
    private String applicationRdfXml;
    private String textRdfN3;
    private String textPlain;
    private String bogusContentType1;
    private String bogusContentType2;
    
    private String trueString;
    private String falseString;
    private String booleanDataType;
    private URI booleanDataTypeUri;
    private Literal trueBooleanTypedLiteral;
    private Literal falseBooleanTypedLiteral;
    private Literal trueBooleanNativeLiteral;
    private Literal falseBooleanNativeLiteral;
    private Literal trueBooleanStringLiteral;
    private Literal falseBooleanStringLiteral;
    
    private String dateTimeDataType;
    private Date testDate;
    private String testDateString;
    private Literal testDateTypedLiteral;
    private Literal testDateNativeLiteral;
    private Literal testDateStringLiteral;
    
    private Literal testFloatTypedLiteral;
    private Literal testFloatNativeLiteral;
    private Literal testFloatStringLiteral;
    private float testFloat;
    private String floatDataType;
    
    private Literal testIntTypedLiteral;
    private Literal testIntNativeLiteral;
    private Literal testIntStringLiteral;
    private int testInt;
    private String intDataType;
    
    private Literal testLongTypedLiteral;
    private Literal testLongNativeLiteral;
    private Literal testLongStringLiteral;
    private long testLong;
    private String longDataType;
    
    private URI testProfileUri1;
    private URI testProfileUri2;
    
    private URI testProviderUri1;
    private URI testRuleTestUri1;
    private URI testRuleTestUri2;
    private URI testQueryTypeUri1;
    private URI testQueryTypeUri2;
    private URI testNormalisationRule1;
    private URI testNormalisationRule2;
    private URI testNormalisationRule3;
    private URI testNormalisationRule4;
    
    private URI testNormalisationRule5;
    
    private Literal testStringLiteral1;
    private Literal testStringLiteral2;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.testRepository = new SailRepository(new MemoryStore());
        this.testRepository.initialize();
        this.testRepositoryConnection = this.testRepository.getConnection();
        this.testValueFactory = new ValueFactoryImpl();
        
        this.applicationRdfXml = "application/rdf+xml";
        this.textRdfN3 = "text/rdf+n3";
        this.textPlain = "text/plain";
        this.bogusContentType1 = "bogus1";
        this.bogusContentType2 = "bogus2";
        
        this.trueString = "true";
        this.falseString = "false";
        this.booleanDataType = "http://www.w3.org/2001/XMLSchema#boolean";
        this.booleanDataTypeUri = this.testValueFactory.createURI(this.booleanDataType);
        this.trueBooleanTypedLiteral = this.testValueFactory.createLiteral(this.trueString, this.booleanDataTypeUri);
        this.falseBooleanTypedLiteral = this.testValueFactory.createLiteral(this.falseString, this.booleanDataTypeUri);
        this.trueBooleanNativeLiteral = this.testValueFactory.createLiteral(true);
        this.falseBooleanNativeLiteral = this.testValueFactory.createLiteral(false);
        this.trueBooleanStringLiteral = this.testValueFactory.createLiteral(this.trueString);
        this.falseBooleanStringLiteral = this.testValueFactory.createLiteral(this.falseString);
        
        this.dateTimeDataType = "http://www.w3.org/2001/XMLSchema#dateTime";
        final Calendar testDateCalendar = Constants.ISO8601UTC().getCalendar();
        testDateCalendar.set(2010, 01, 02, 03, 04, 05);
        this.testDate = testDateCalendar.getTime();
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(this.testDate.getTime());
        DatatypeFactory df;
        try
        {
            df = DatatypeFactory.newInstance();
        }
        catch(final DatatypeConfigurationException dce)
        {
            throw new IllegalStateException("Exception while obtaining DatatypeFactory instance", dce);
        }
        final XMLGregorianCalendar xmlDate = df.newXMLGregorianCalendar(gc);
        this.testDateString = Constants.ISO8601UTC().format(this.testDate);
        this.testDateTypedLiteral = this.testValueFactory.createLiteral(this.testDateString, this.dateTimeDataType);
        this.testDateNativeLiteral = this.testValueFactory.createLiteral(xmlDate);
        this.testDateStringLiteral = this.testValueFactory.createLiteral(this.testDateString);
        
        this.floatDataType = "http://www.w3.org/2001/XMLSchema#float";
        this.testFloat = 0.278134f;
        this.testFloatTypedLiteral =
                this.testValueFactory.createLiteral(Float.toString(this.testFloat), this.floatDataType);
        this.testFloatNativeLiteral = this.testValueFactory.createLiteral(this.testFloat);
        this.testFloatStringLiteral = this.testValueFactory.createLiteral(Float.toString(this.testFloat));
        
        this.intDataType = "http://www.w3.org/2001/XMLSchema#int";
        this.testInt = 278134;
        this.testIntTypedLiteral =
                this.testValueFactory.createLiteral(Integer.toString(this.testInt), this.intDataType);
        this.testIntNativeLiteral = this.testValueFactory.createLiteral(this.testInt);
        this.testIntStringLiteral = this.testValueFactory.createLiteral(Integer.toString(this.testInt));
        
        this.longDataType = "http://www.w3.org/2001/XMLSchema#long";
        this.testLong = 278134965145L;
        this.testLongTypedLiteral =
                this.testValueFactory.createLiteral(Long.toString(this.testLong), this.longDataType);
        this.testLongNativeLiteral = this.testValueFactory.createLiteral(this.testLong);
        this.testLongStringLiteral = this.testValueFactory.createLiteral(Long.toString(this.testLong));
        
        this.testStringLiteral1 = this.testValueFactory.createLiteral("Simple ASCII Test");
        this.testStringLiteral2 =
                this.testValueFactory
                        .createLiteral("Brisbane ] ist die Hauptstadt des Bundesstaates Queensland im Nordosten Australiens. Die Stadt liegt am Brisbane River nahe an dessen Mündung in die Korallensee. Der Ballungsraum hat 2 Mio. Einwohner. Brisbane ist sowohl katholischer als auch anglikanischer Erzbischofssitz. Ein wichtiger Wirtschaftszweig ist die Erdölindustrie. Brisbane wurde 1824 unter dem Namen Moreton Bay als Strafkolonie gegründet und wurde später nach dem damaligen Gouverneur von New South Wales, Sir Thomas Brisbane benannt. Brisbane ist bekannt für seine Hochschulen, sowie auch für die nördlich und südlich der Stadtgrenze beginnenden Ferienparadiese Sunshine Coast und Gold Coast, für die Meereslage und das allgemein gute Wetter. Im von Abkürzungen geprägten australischen Englisch wird Brisbane gelegentlich auch als Brissie bezeichnet. Darüber hinaus existieren scherzhafte Namensabwandlungen wie Bris Vegas oder Brisneyland, die allerdings nur vereinzelt verwendet werden. Brisbane, is the capital and most populous city in the Australian state of Queensland and the third most populous city in Australia. Brisbane's metropolitan area has a population of over 2 million and constitutes the core of the South East Queensland agglomeration, encompassing more than 3 million people. The Brisbane central business district stands on the original European settlement and is situated inside a bend of the Brisbane River approximately 23 kilometres from its mouth at Moreton Bay. The metropolitan area extends in all directions along the floodplain of the Brisbane River valley between the bay and the Great Dividing Range. While the metropolitan area is governed by several municipalities, a large proportion of central Brisbane is governed by the Brisbane City Council which is Australia's largest Local Government Area by population. Brisbane is named after the river on which it sits which, in turn, was named after Sir Thomas Brisbane, the Governor of New South Wales from 1821 to 1825. The first European settlement in Queensland was a penal colony at Redcliffe, 28 kilometres (17 mi) north of the Brisbane central business district, in 1824. That settlement was soon abandoned and moved to North Quay in 1825. Free settlers were permitted from 1842. Brisbane was chosen as the capital when Queensland was proclaimed a separate colony from New South Wales in 1859. The city played a central role in the Allied campaign during World War II as the South West Pacific headquarters for General Douglas MacArthur. Brisbane has hosted many large cultural and sporting events including the 1982 Commonwealth Games, World Expo '88 and the final Goodwill Games in 2001. Brisbane is the largest economy between Sydney and Singapore and in 2008 it was classified as a gamma world city+ in the World Cities Study Group’s inventory by Loughborough University. It was also rated the 16th most livable city in the world in 2009 by The Economist. Este artículo trata sobre la ciudad de Brisbane. Para saber más sobre el río, véase Río Brisbane. Plantilla:Ficha de localidad Brisbane es la tercera ciudad más grande de Australia. Es la capital del estado de Queensland, en el noreste del país y tiene aproximadamente dos millones de habitantes (estimado en el 2009). Se encuentra al este de la Gran Cordillera Divisoria, al sureste de la cordillera Taylor y muy cercana a la bahía Moretón. Es atravesada por el río Brisbane, que ha sido dragado para facilitar el tránsito de barcos. Hacia el oeste el horizonte está dominado por la presencia del monte Cootha en cuyas faldas se encuentra un planetario y los jardines botánicos. En su cima se encuentra un mirador que ofrece unas vistas magníficas de la ciudad y su ondulante río. Las más importantes estaciones de televisión en el estado de Queensland tienen sus estudios en el monte Cootha. El sector de Southbank, que se ubica en la ribera sur del río Brisbane, se renovó intensamente para acoger la Exposición Internacional de 1988, pasando a convertirse de sus orígenes industriales a un agradable parque de hermosos jardines, donde también se encuentra una playa pública artificial, restaurantes y bares, espacios de esparcimiento y el museo marítimo. Con la construcción del puente peatonal Good Will se puede llegar fácilmente desde el centro de la ciudad a Southbank en tan sólo unos minutos. El área es una de las favoritas de los habitantes de Brisbane para celebrar asados familiares los fines de semana. El municipio de Brisbane ha anunciado un plan de desarrollo que considera la construcción de puentes peatonales adicionales con el fin de hacer la ciudad más accesible a peatones y ciclistas, así como también para incentivar las actividades físicas. La economía se basa en diversas industrias petroquímicas, metalúrgicas, construcciones mecánicas, alimentarias y ferrometalúrgicas. Destaca también su puerto, por el cual exporta carbón y metales. Es también un importante centro cultural y turístico, además de ser centro de nudo ferroviario y aéreo para Queensland. En sus proximidades se encuentra la terminal de un gasoducto y la del oleoducto de Moonie. Brisbane on Queenslandin osavaltion pääkaupunki ja sen suurin kaupunki, mutta myös koko Australian kolmanneksi suurin kaupunki vajaalla kahdella miljoonalla asukkaallaan. Kaupunki sijaitsee Tyynenmeren läheisyydessä Brisbanejoen rannalla Moreton Bayn ja Australian Kordillieerien välisellä rannikkotasangolla Queenslandin kaakkoisosassa. Brisbane è la città più popolosa nello stato australiano del Queensland e la terza città più popolosa dell'Australia. L'area metropolitana di Brisbane ha una popolazione di circa 2 milioni di abitanti. Un residente di Brisbane è comunemente chiamato \"Brisbanite\". Il distretto centrale degli affari di Brisbane è situato sull'insediamento originale, posto all'interno di un'ansa del fiume Brisbane, approssimativamente a 23 km dalla sua foce a Moreton Bay. L'area metropolitana si estende in tutte le direzioni lungo la piana alluvionale della valle del fiume Brisbane, tra la baia e la Gran Catena Divisoria. Poiché la città è governata da numerose municipalità, queste sono accentrate attorno al Concilio della Città di Brisbane che ha giurisdizione sulla più vasta area e popolazione nella Brisbane metropolitana ed è anche l'Area di Governo australiano locale più grande per popolazione. Brisbane prende il nome dal fiume sul quale è sita che, a sua volta, fu chiamato così da Sir Thomas Brisbane, il Governatore del Nuovo Galles del Sud dal 1821 al 1825. Il primo insediamento europeo nel Queensland fu una colonia penale a Redcliffe, 28 km a nord del distretto degli affari di Brisbane, nel 1824. Quell'insediamento fu presto abbandonato e trasferito a North Quay nel 1825. Brisbane fu scelta come capitale quando il Queensland venne proclamato colonia separata dal Nuovo Galles del Sud nel 1859. La città ha giocato un ruolo chiave durante la Seconda Guerra Mondiale, in quanto quartier generale del Pacifico del Sud-Ovest del Generale Douglas MacArthur. Brisbane ha ospitato molti grandi eventi culturali e sportivi fra cui i Giochi del Commonwealth nel 1982, l'Esposizione universale nel 1988 e la finale dei Goodwill Games nel 2001. Nel 2008, Brisbane è stata classificata come \"gamma world city+\" nell'inventario del World Cities Study Group dall'università di Loughborough. È stata anche dichiarata 16ima città più abitabile nel mondo nel 2009 da \"The Economist\". ブリスベン（Brisbane）はオーストラリア連邦クイーンズランド州南東部（サウス・イースト・クイーンズランド地域）に位置する州都。 シドニー、メルボルンに次ぐオーストラリア第三の都市であり、オセアニア有数の世界都市。現地での発音はTemplate:IPA（ブリズベン）であるが、ここでは日本の外務省の表記にならった。 Brisbane is een stad in het oosten van Australië. Het is de hoofdstad van de deelstaat Queensland. De stad is gelegen aan de oostkust van Australië aan de Brisbane River, zo'n 20 kilometer verwijderd van Moreton Bay. Brisbane heeft 1.676.389 inwoners (2006) en is daarmee de derde stad van Australië, na Sydney en Melbourne. Brisbane is vooral bij jonge toeristen populair vanwege de twee grote uitgaansgebieden in de stad. In deze gebieden zijn veel clubs, bars, eet- en uitgaansgelegenheden. De stad heeft een subtropisch klimaat met warme zomers en zeer milde winters. Brisbane ging in 1824 als strafkolonie van start en is vernoemd naar Sir Thomas Brisbane, toenmalig gouverneur van de deelstaat Nieuw-Zuid-Wales. Toen Queensland in 1859 tot aparte deelstaat werd uitgeroepen werd Brisbane tot hoofdstad gekozen. Tot de Tweede Wereldoorlog ontwikkelde de stad zich langzaam. Tijdens WOII was het geallieerde hoofdkwartier van Generaal Douglas MacArthur in Brisbane gevestigd en speelde de stad een centrale rol in de strijd in het zuidwestelijk deel van de Stille Oceaan. In 1982 organiseerde Brisbane de Gemenebestspelen en in 1988 vond de Wereldtentoonstelling er plaats. Vanaf die periode heeft de stad een snelle groei doorgemaakt en is zij uitgegroeid tot de huidige metropool. Brisbane [/Mal:IPA/] er hovedstaden i den australske delstaten Queensland. Byen har ca. 1,8 millioner innbyggere, er Australias raskest voksende by og den tredje største byen i Australia etter Sydney og Melbourne. Gjennom byen renner Brisbane River. Brisbane [ˈbɹɪzbən ˈbɹɪzbən] – miasto w Australii, stolica stanu Queensland, położone u ujścia rzeki Brisbane do zatoki Moreton. Klimat subtropikalny z gorącymi, wilgotnymi latami i ciepłymi, łagodnymi zimami. Ważny ośrodek handlowy, naukowy (3 uniwersytety) i kulturalny (muzea, galeria sztuki); liczne parki (herbarium). Brisbane jest ośrodkiem przemysłu rafineryjnego, gumowego, stoczniowego i maszynowego. Odbywało się tu wiele znaczących wydarzeń kulturalnych i sportowych, m. in. Igrzyska Wspólnoty Narodów (Commonwealth Games) w 1982 r. , Wystawa Światowa (World Expo) w 1988 r. oraz Igrzyska Dobrej Woli w 2001 r. W mieście znajduje się polski konsulat honorowy. Brisbane é a capital do estado de Queensland e terceira maior cidade da Austrália. Brisbane [uttal:ˈbɹɪzbən uttal:ˈbɹɪzbən] är en stad i Australien med 1,9 miljoner invånare. Den är huvudstad i delstaten Queensland samt den tredje största staden i Australien och största stad i Queensland. Brisbane är byggd utefter Brisbane River, som slingrar sig igenom staden och korsas av flera broar. Namnet har staden fått efter Sir Thomas Brisbane, som var guvernör i New South Wales åren 1821-1825. 布里斯班（Template:Lang-en），是澳大利亞昆士蘭州府城，位於澳大利亞本土的東北部，北緣陽光海岸， 南鄰國際觀光勝地黃金海岸市。大都會區人口（包括週圍的衛星城市）200萬餘，是澳大利亞人口第三大都會，僅次於雪梨與墨爾本。 布里斯本靠近太平洋，東面濱臨塔斯曼海，是一個從海岸線、河川和港口往西部內陸發展的都市。其市中心位於布里斯本河畔（Brisbane River），該州即以此為政治和交通主軸再向南北伸展開發。布里斯本國內外機場和國際海港座落於布里斯本河口兩旁。 布里斯本是1982年英聯邦運動會、1988年世界博覽會，以及2001年世界友誼運動會（Goodwill Games）的主辦城市。 Бри́сбен — крупный город на восточном побережье Австралии. Административный центр штата Квинсленд. Население — 1,8 млн человек, это третий по численности город страны. Расположен в излучине Реки Брисбен приблизительно 23 км от ее устья. Brisbane est la capitale et la ville la plus peuplée de l'État du Queensland, en Australie. Située à environ 950 kilomètres au nord de Sydney, sur le fleuve Brisbane, elle s'étend sur une plaine humide bordée de collines, limitée par Moreton Bay et par les premiers contreforts de la cordillère australienne. À quelques kilomètres du centre-ville, le mont Coot-Tha accueille une plate-forme panoramique, un planetarium et des jardins botaniques. Le site de Brisbane est appelé « Mian-Jin » par les aborigènes Turrbal, ce qui signifie « L'endroit pointu ». Troisième ville d'Australie en termes de population, elle compterait 2 millions d'habitants en tenant compte de sa périphérie (Grand Brisbane). La ville doit son nom à sir Thomas Brisbane, le gouverneur de Nouvelle-Galles du Sud de 1821 à 1825. Fondée en 1824 à Redcliffe, à 28 kilomètres du centre-ville actuel, la colonie pénale de Brisbane fut ensuite déplacée en amont, dans une boucle du fleuve Brisbane. Les premiers colons libres purent s'installer à Brisbane en 1842, peu après la fermeture du centre pénitentiaire. En 1859, la scission du nord de la Nouvelle-Galles du Sud donna lieu à la création du Queensland, dont Brisbane devint la capitale. La ville joua un grand rôle pour les forces alliées pendant la Seconde Guerre mondiale et servit de quartier général au général Douglas MacArthur qui commandait les forces alliées du sud-ouest du Pacifique. Plus récemment Brisbane a accueilli les jeux du Commonwealth de 1982, l'exposition universelle Expo '88, et les Goodwill Games de 2001. L'économie de Brisbane est basée sur les industries pétrochimiques, métallurgiques, agroalimentaires et mécaniques. Son port permet l'exportation des ressources naturelles de l'État (charbon, argent, plomb, zinc). La ville est également un important centre culturel et touristique, à peu de distance des stations balnéaires de la Sunshine Coast et de la Gold Coast.");
        
        this.testProfileUri1 = this.testValueFactory.createURI("http://example.org/profile:test-1");
        this.testProfileUri2 = this.testValueFactory.createURI("http://example.org/profile:test-2");
        
        this.testProviderUri1 = this.testValueFactory.createURI("http://example.org/provider:test-1");
        
        this.testRuleTestUri1 = this.testValueFactory.createURI("http://example.org/ruletest:test-1");
        this.testRuleTestUri2 = this.testValueFactory.createURI("http://example.org/ruletest:test-2");
        
        this.testQueryTypeUri1 = this.testValueFactory.createURI("http://example.org/query:test-1");
        this.testQueryTypeUri2 = this.testValueFactory.createURI("http://example.org/query:test-2");
        
        this.testNormalisationRule1 = this.testValueFactory.createURI("http://example.org/rdfrule:abc_issn");
        this.testNormalisationRule2 =
                this.testValueFactory.createURI("http://bio2rdf.org/rdfrule:neurocommonsgeneaddsymboluriconstruct");
        this.testNormalisationRule3 = this.testValueFactory.createURI("http://bio2rdf.org/rdfrule:xsltNlmPubmed");
        this.testNormalisationRule4 = this.testValueFactory.createURI("http://oas.example.org/rdfrule:bio2rdfpo");
        this.testNormalisationRule5 =
                this.testValueFactory.createURI("http://bio2rdf.org/rdfrule:neurocommonsgeneaddsymboluriask");
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        if(this.testRepositoryConnection != null)
        {
            try
            {
                this.testRepositoryConnection.close();
            }
            catch(final RepositoryException ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                this.testRepositoryConnection = null;
            }
        }
        
        this.testRepository = null;
        this.testValueFactory = null;
        
        this.applicationRdfXml = null;
        this.textRdfN3 = null;
        this.textPlain = null;
        this.bogusContentType1 = null;
        this.bogusContentType2 = null;
        
        this.trueString = null;
        this.falseString = null;
        this.booleanDataType = null;
        this.booleanDataTypeUri = null;
        
        this.trueBooleanTypedLiteral = null;
        this.falseBooleanTypedLiteral = null;
        
        this.trueBooleanNativeLiteral = null;
        this.falseBooleanNativeLiteral = null;
        
        this.trueBooleanStringLiteral = null;
        this.falseBooleanStringLiteral = null;
        
        this.testProfileUri1 = null;
        this.testProfileUri2 = null;
        
        this.testProviderUri1 = null;
        
        this.testRuleTestUri1 = null;
        
        this.testQueryTypeUri1 = null;
        this.testNormalisationRule1 = null;
        this.testNormalisationRule2 = null;
        this.testNormalisationRule3 = null;
        this.testNormalisationRule4 = null;
        this.testNormalisationRule5 = null;
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#findBestContentType(java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testFindBestContentTypeAgainst1BogusType()
    {
        Assert.assertEquals(this.applicationRdfXml,
                RdfUtils.findBestContentType(this.applicationRdfXml, this.bogusContentType1, this.textRdfN3));
        Assert.assertEquals(this.applicationRdfXml,
                RdfUtils.findBestContentType(this.bogusContentType1, this.applicationRdfXml, this.textRdfN3));
        Assert.assertEquals(this.textRdfN3,
                RdfUtils.findBestContentType(this.bogusContentType1, this.textRdfN3, this.applicationRdfXml));
        
        Assert.assertEquals(this.textRdfN3,
                RdfUtils.findBestContentType(this.textRdfN3, this.bogusContentType1, this.applicationRdfXml));
        Assert.assertEquals(this.textRdfN3,
                RdfUtils.findBestContentType(this.bogusContentType1, this.textRdfN3, this.applicationRdfXml));
        Assert.assertEquals(this.applicationRdfXml,
                RdfUtils.findBestContentType(this.bogusContentType1, this.applicationRdfXml, this.textRdfN3));
        
        Assert.assertEquals(this.textPlain,
                RdfUtils.findBestContentType(this.textPlain, this.bogusContentType1, this.applicationRdfXml));
        Assert.assertEquals(this.textPlain,
                RdfUtils.findBestContentType(this.bogusContentType1, this.textPlain, this.applicationRdfXml));
        Assert.assertEquals(this.applicationRdfXml,
                RdfUtils.findBestContentType(this.bogusContentType1, this.applicationRdfXml, this.textPlain));
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#findBestContentType(java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testFindBestContentTypeAgainst2BogusTypes()
    {
        Assert.assertEquals(this.applicationRdfXml,
                RdfUtils.findBestContentType(this.applicationRdfXml, this.bogusContentType1, this.bogusContentType2));
        Assert.assertEquals(this.applicationRdfXml,
                RdfUtils.findBestContentType(this.bogusContentType1, this.applicationRdfXml, this.bogusContentType2));
        Assert.assertEquals(this.applicationRdfXml,
                RdfUtils.findBestContentType(this.bogusContentType1, this.bogusContentType2, this.applicationRdfXml));
        
        Assert.assertEquals(this.textRdfN3,
                RdfUtils.findBestContentType(this.textRdfN3, this.bogusContentType1, this.bogusContentType2));
        Assert.assertEquals(this.textRdfN3,
                RdfUtils.findBestContentType(this.bogusContentType1, this.textRdfN3, this.bogusContentType2));
        Assert.assertEquals(this.textRdfN3,
                RdfUtils.findBestContentType(this.bogusContentType1, this.bogusContentType2, this.textRdfN3));
        
        Assert.assertEquals(this.textPlain,
                RdfUtils.findBestContentType(this.textPlain, this.bogusContentType1, this.bogusContentType2));
        Assert.assertEquals(this.textPlain,
                RdfUtils.findBestContentType(this.bogusContentType1, this.textPlain, this.bogusContentType2));
        Assert.assertEquals(this.textPlain,
                RdfUtils.findBestContentType(this.bogusContentType1, this.bogusContentType2, this.textPlain));
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#findBestContentType(java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testFindBestContentTypeWith3RealTypes()
    {
        Assert.assertEquals(this.applicationRdfXml,
                RdfUtils.findBestContentType(this.applicationRdfXml, this.textPlain, this.textRdfN3));
        Assert.assertEquals(this.textPlain,
                RdfUtils.findBestContentType(this.textPlain, this.applicationRdfXml, this.textRdfN3));
        Assert.assertEquals(this.textPlain,
                RdfUtils.findBestContentType(this.textPlain, this.textRdfN3, this.applicationRdfXml));
        
        Assert.assertEquals(this.textRdfN3,
                RdfUtils.findBestContentType(this.textRdfN3, this.textPlain, this.applicationRdfXml));
        Assert.assertEquals(this.textPlain,
                RdfUtils.findBestContentType(this.textPlain, this.textRdfN3, this.applicationRdfXml));
        Assert.assertEquals(this.textPlain,
                RdfUtils.findBestContentType(this.textPlain, this.applicationRdfXml, this.textRdfN3));
        
        Assert.assertEquals(this.textPlain,
                RdfUtils.findBestContentType(this.textPlain, this.textRdfN3, this.applicationRdfXml));
        Assert.assertEquals(this.textRdfN3,
                RdfUtils.findBestContentType(this.textRdfN3, this.textPlain, this.applicationRdfXml));
        Assert.assertEquals(this.textRdfN3,
                RdfUtils.findBestContentType(this.textRdfN3, this.applicationRdfXml, this.textPlain));
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#getDateTimeFromValue(org.openrdf.model.Value)}.
     * 
     * 
     * TODO: make this work
     */
    @Test
    public void testGetDateTimeFromValue()
    {
        try
        {
            Assert.assertEquals(this.testDate.getTime(), RdfUtils.getDateTimeFromValue(this.testDateTypedLiteral)
                    .getTime(), 1000);
        }
        catch(final ParseException e)
        {
            Assert.fail("Found unexpected ParseException e=" + e.getMessage());
        }
        
        try
        {
            Assert.assertEquals(this.testDate.getTime(), RdfUtils.getDateTimeFromValue(this.testDateStringLiteral)
                    .getTime(), 1000);
        }
        catch(final ParseException e)
        {
            Assert.fail("Found unexpected ParseException e=" + e.getMessage());
        }
        
        try
        {
            Assert.assertEquals(this.testDate.getTime(), RdfUtils.getDateTimeFromValue(this.testDateNativeLiteral)
                    .getTime());
        }
        catch(final ParseException e)
        {
            // TODO: Make this work
            // Assert.fail("Found unexpected ParseException e=" + e.getMessage());
        }
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#getFloatFromValue(org.openrdf.model.Value)}.
     */
    @Test
    public void testGetFloatFromValue()
    {
        Assert.assertEquals(this.testFloat, RdfUtils.getFloatFromValue(this.testFloatTypedLiteral), 0.0000001);
        Assert.assertEquals(this.testFloat, RdfUtils.getFloatFromValue(this.testFloatNativeLiteral), 0.0000001);
        Assert.assertEquals(this.testFloat, RdfUtils.getFloatFromValue(this.testFloatStringLiteral), 0.0000001);
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#getIntegerFromValue(org.openrdf.model.Value)}.
     */
    @Test
    public void testGetIntegerFromValue()
    {
        Assert.assertEquals(this.testInt, RdfUtils.getIntegerFromValue(this.testIntTypedLiteral));
        Assert.assertEquals(this.testInt, RdfUtils.getIntegerFromValue(this.testIntNativeLiteral));
        Assert.assertEquals(this.testInt, RdfUtils.getIntegerFromValue(this.testIntStringLiteral));
    }
    
    /**
     * Test method for {@link org.queryall.utils.RdfUtils#getLongFromValue(org.openrdf.model.Value)}
     * .
     */
    @Test
    public void testGetLongFromValue()
    {
        Assert.assertEquals(this.testLong, RdfUtils.getLongFromValue(this.testLongTypedLiteral));
        Assert.assertEquals(this.testLong, RdfUtils.getLongFromValue(this.testLongNativeLiteral));
        Assert.assertEquals(this.testLong, RdfUtils.getLongFromValue(this.testLongStringLiteral));
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#getNamespaceEntries(org.openrdf.repository.Repository)}.
     */
    @Test
    public void testGetNamespaceEntries()
    {
        final InputStream nextInputStream = this.getClass().getResourceAsStream("/testconfigs/namespaceentry-1.n3");
        
        try
        {
            Assert.assertNotNull("Could not find test file", nextInputStream);
            
            this.testRepositoryConnection.add(nextInputStream, "", RDFFormat.N3);
            this.testRepositoryConnection.commit();
            
            final Map<URI, NamespaceEntry> results = RdfUtils.getNamespaceEntries(this.testRepository);
            
            Assert.assertEquals(1, results.size());
            
            for(final URI nextNamespaceEntryUri : results.keySet())
            {
                Assert.assertEquals("Results did not contain correct namespace entry URI",
                        this.testValueFactory.createURI("http://example.org/ns:abc"), nextNamespaceEntryUri);
                
                final NamespaceEntry nextNamespaceEntry = results.get(nextNamespaceEntryUri);
                
                Assert.assertNotNull("Namespace entry was null", nextNamespaceEntry);
                
                Assert.assertEquals("Namespace entry key was not the same as its map URI", nextNamespaceEntryUri,
                        nextNamespaceEntry.getKey());
                
                Assert.assertEquals("Authority was not parsed correctly",
                        this.testValueFactory.createURI("http://example.org/"), nextNamespaceEntry.getAuthority());
                
                Assert.assertEquals("URI template was not parsed correctly",
                        "${authority}${namespace}${separator}${identifier}", nextNamespaceEntry.getUriTemplate());
                
                Assert.assertEquals("Separator was not parsed correctly", ":", nextNamespaceEntry.getSeparator());
                
                Assert.assertEquals("Preferred prefix was not parsed correctly", "abc",
                        nextNamespaceEntry.getPreferredPrefix());
                
                Assert.assertTrue("Convert queries to preferred prefix setting was not parsed correctly",
                        nextNamespaceEntry.getConvertQueriesToPreferredPrefix());
                
                Assert.assertEquals("Description was not parsed correctly", "ABC Example Database",
                        nextNamespaceEntry.getDescription());
                
                Assert.assertEquals("QueryAllNamespace was not implemented correctly for this object",
                        QueryAllNamespaces.NAMESPACEENTRY, nextNamespaceEntry.getDefaultNamespace());
                
                Assert.assertTrue("Was not a validating namespace",
                        nextNamespaceEntry instanceof ValidatingNamespaceEntry);
                
                final ValidatingNamespaceEntry nextValidatingNamespaceEntry =
                        (ValidatingNamespaceEntry)nextNamespaceEntry;
                
                Assert.assertTrue("Was not a regex validating namespace",
                        nextNamespaceEntry instanceof ValidatingNamespaceEntry);
                
                Assert.assertTrue("Validation possible field was not parsed correctly",
                        nextValidatingNamespaceEntry.getValidationPossible());
                
                final RegexValidatingNamespaceEntry nextRegexValidatingNamespaceEntry =
                        (RegexValidatingNamespaceEntry)nextValidatingNamespaceEntry;
                
                Assert.assertEquals("Identifier Regex was not parsed correctly", "[zyx][qrs][tuv]",
                        nextRegexValidatingNamespaceEntry.getIdentifierRegex());
                
                // once we have found that the regex was parsed correctly through the
                // RegexValidatingNamespaceEntry interface, go back through ValidatingNamespaceEntry
                // and validate a test identifier
                Assert.assertTrue("Validation failed", nextValidatingNamespaceEntry.validateIdentifier("zrv"));
                
                if(RdfUtilsTest.FAIL_ON_UNEXPECTED_TRIPLES)
                {
                    Assert.assertEquals("There were unexpected triples in the test file. This should not happen. "
                            + nextNamespaceEntry.getUnrecognisedStatements(), 0, nextNamespaceEntry
                            .getUnrecognisedStatements().size());
                }
            }
        }
        catch(final RDFParseException ex)
        {
            Assert.fail("Found unexpected RDFParseException : " + ex.getMessage());
        }
        catch(final RepositoryException ex)
        {
            Assert.fail("Found unexpected RepositoryException : " + ex.getMessage());
        }
        catch(final IOException ex)
        {
            Assert.fail("Found unexpected IOException : " + ex.getMessage());
        }
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#getBooleanFromValue(org.openrdf.model.Value)}.
     */
    @Test
    public void testGetNativeBooleanFromValue()
    {
        Assert.assertTrue(RdfUtils.getBooleanFromValue(this.trueBooleanNativeLiteral));
        Assert.assertFalse(RdfUtils.getBooleanFromValue(this.falseBooleanNativeLiteral));
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#getNormalisationRules(org.openrdf.repository.Repository)}.
     * 
     */
    @Test
    public void testGetNormalisationRules()
    {
        final InputStream nextInputStream = this.getClass().getResourceAsStream("/testconfigs/normalisationrule-1.n3");
        
        try
        {
            Assert.assertNotNull("Could not find test file", nextInputStream);
            
            this.testRepositoryConnection.add(nextInputStream, "", RDFFormat.N3);
            this.testRepositoryConnection.commit();
            
            final Map<URI, NormalisationRule> results = RdfUtils.getNormalisationRules(this.testRepository);
            
            Assert.assertEquals("RdfUtils did not create the expected number of normalisation rules.", 5,
                    results.size());
            
            for(final URI nextNormalisationRuleUri : results.keySet())
            {
                final NormalisationRule nextNormalisationRule = results.get(nextNormalisationRuleUri);
                
                Assert.assertNotNull("Normalisation rule was null", nextNormalisationRule);
                
                Assert.assertEquals("Normalisation rule key was not the same as its map URI", nextNormalisationRuleUri,
                        nextNormalisationRule.getKey());
                
                if(nextNormalisationRuleUri.equals(this.testNormalisationRule1))
                {
                    Assert.assertEquals("Results did not contain correct normalisation rule URI",
                            this.testNormalisationRule1, nextNormalisationRule.getKey());
                    
                    Assert.assertEquals("Did not find expected number of stages", 2, nextNormalisationRule.getStages()
                            .size());
                    Assert.assertTrue(
                            "Could not find expected stage",
                            nextNormalisationRule.getStages().contains(
                                    NormalisationRuleSchema.getRdfruleStageQueryVariables()));
                    Assert.assertTrue(
                            "Could not find expected stage",
                            nextNormalisationRule.getStages().contains(
                                    NormalisationRuleSchema.getRdfruleStageBeforeResultsImport()));
                    
                    Assert.assertEquals(
                            "Description was not parsed correctly",
                            "Converts between the URIs used by the ABC ISSN's and the Example organisation ISSN namespace",
                            nextNormalisationRule.getDescription());
                    Assert.assertEquals("Order was not parsed correctly", 110, nextNormalisationRule.getOrder());
                    Assert.assertEquals("Include exclude order was not parsed correctly",
                            ProfileSchema.getProfileIncludeThenExcludeUri(),
                            nextNormalisationRule.getProfileIncludeExcludeOrder());
                    
                    Assert.assertEquals("Related namespaces were not parsed correctly", 1, nextNormalisationRule
                            .getRelatedNamespaces().size());
                    
                    Assert.assertTrue(
                            "Related namespace was not parsed correctly",
                            nextNormalisationRule.getRelatedNamespaces().contains(
                                    this.testValueFactory.createURI("http://example.org/ns:issn")));
                    
                    final RegexNormalisationRule nextRegexRule = (RegexNormalisationRule)nextNormalisationRule;
                    
                    Assert.assertEquals("Regex rule input match regex was not parsed correctly",
                            "http://example\\.org/issn:", nextRegexRule.getInputMatchRegex());
                    Assert.assertEquals("Regex rule input replace regex was not parsed correctly",
                            "http://id\\.abc\\.org/issn/", nextRegexRule.getInputReplaceRegex());
                    
                    Assert.assertEquals("Regex rule output match regex was not parsed correctly",
                            "http://id\\.abc\\.org/issn/", nextRegexRule.getOutputMatchRegex());
                    Assert.assertEquals("Regex rule output replace regex was not parsed correctly",
                            "http://example\\.org/issn:", nextRegexRule.getOutputReplaceRegex());
                }
                else if(nextNormalisationRuleUri.equals(this.testNormalisationRule2))
                {
                    Assert.assertEquals("Results did not contain correct normalisation rule URI",
                            this.testNormalisationRule2, nextNormalisationRule.getKey());
                    
                    Assert.assertEquals("Did not find expected number of stages", 1, nextNormalisationRule.getStages()
                            .size());
                    Assert.assertTrue(
                            "Could not find expected stage",
                            nextNormalisationRule.getStages().contains(
                                    NormalisationRuleSchema.getRdfruleStageAfterResultsToPool()));
                    
                    Assert.assertEquals("Description was not parsed correctly",
                            "Add symbol URI based on Neurocommons gene symbol literals",
                            nextNormalisationRule.getDescription());
                    Assert.assertEquals("Order was not parsed correctly", 100, nextNormalisationRule.getOrder());
                    Assert.assertEquals("Include exclude order was not parsed correctly",
                            ProfileSchema.getProfileExcludeThenIncludeUri(),
                            nextNormalisationRule.getProfileIncludeExcludeOrder());
                    
                    Assert.assertEquals("Related namespaces were not parsed correctly", 1, nextNormalisationRule
                            .getRelatedNamespaces().size());
                    
                    Assert.assertTrue(
                            "Related namespace was not parsed correctly",
                            nextNormalisationRule.getRelatedNamespaces().contains(
                                    this.testValueFactory.createURI("http://example.org/ns:symbol")));
                    
                    Assert.assertTrue(
                            "Normalisation rule was not implemented using the SparqlNormalisationRule interface",
                            nextNormalisationRule instanceof SparqlNormalisationRule);
                    
                    final SparqlNormalisationRule nextSparqlRule = (SparqlNormalisationRule)nextNormalisationRule;
                    
                    Assert.assertEquals("Did not parse the correct number of sparql where patterns", 1, nextSparqlRule
                            .getSparqlWherePatterns().size());
                    
                    Assert.assertEquals(
                            "Sparql construct query where pattern was not parsed correctly",
                            " ?myUri <http://purl.org/science/owl/sciencecommons/ggp_has_primary_symbol> ?primarysymbol . bind(iri(concat(\"http://bio2rdf.org/symbol:\", encode_for_uri(lcase(str(?primarySymbol))))) AS ?symbolUri)",
                            nextSparqlRule.getSparqlWherePatterns().iterator().next());
                    
                    Assert.assertTrue("Normalisation rule was not implemented using the SparqlConstructRule interface",
                            nextNormalisationRule instanceof SparqlConstructRule);
                    
                    final SparqlConstructRule nextSparqlConstructRule = (SparqlConstructRule)nextSparqlRule;
                    
                    Assert.assertEquals("Sparql mode not parsed correctly",
                            SparqlConstructRuleSchema.getSparqlRuleModeAddAllMatchingTriples(),
                            nextSparqlConstructRule.getMode());
                    
                    Assert.assertEquals("Sparql construct query target was not parsed correctly",
                            "?myUri <http://bio2rdf.org/bio2rdf_resource:dbxref> ?symbolUri . ",
                            nextSparqlConstructRule.getSparqlConstructQueryTarget());
                    
                }
                else if(nextNormalisationRuleUri.equals(this.testNormalisationRule3))
                {
                    Assert.assertEquals("Results did not contain correct normalisation rule URI",
                            this.testNormalisationRule3, nextNormalisationRule.getKey());
                    
                    Assert.assertEquals("Did not find expected number of stages", 1, nextNormalisationRule.getStages()
                            .size());
                    Assert.assertTrue(
                            "Could not find expected stage",
                            nextNormalisationRule.getStages().contains(
                                    NormalisationRuleSchema.getRdfruleStageBeforeResultsImport()));
                    
                    Assert.assertEquals("Description was not parsed correctly",
                            "XSLT transformation of a Pubmed XML document into RDF NTriples",
                            nextNormalisationRule.getDescription());
                    Assert.assertEquals("Order was not parsed correctly", 100, nextNormalisationRule.getOrder());
                    Assert.assertEquals("Include exclude order was not parsed correctly",
                            ProfileSchema.getProfileExcludeThenIncludeUri(),
                            nextNormalisationRule.getProfileIncludeExcludeOrder());
                    
                    Assert.assertEquals("Related namespaces were not parsed correctly", 1, nextNormalisationRule
                            .getRelatedNamespaces().size());
                    
                    Assert.assertTrue(
                            "Related namespace was not parsed correctly",
                            nextNormalisationRule.getRelatedNamespaces().contains(
                                    this.testValueFactory.createURI("http://example.org/ns:pubmed")));
                    
                    Assert.assertTrue(
                            "Normalisation rule was not implemented using the XsltNormalisationRule interface",
                            nextNormalisationRule instanceof XsltNormalisationRule);
                    
                    final XsltNormalisationRule nextXsltRule = (XsltNormalisationRule)nextNormalisationRule;
                    
                    Assert.assertTrue("Xslt transform was not parsed correctly", nextXsltRule.getXsltStylesheet()
                            .startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"));
                    
                    Assert.assertTrue("Xslt transform was not parsed correctly", nextXsltRule.getXsltStylesheet()
                            .contains("</xsl:stylesheet>"));
                }
                else if(nextNormalisationRuleUri.equals(this.testNormalisationRule4))
                {
                    Assert.assertEquals("Results did not contain correct normalisation rule URI",
                            this.testNormalisationRule4, nextNormalisationRule.getKey());
                    
                    Assert.assertEquals("Did not find expected number of stages", 3, nextNormalisationRule.getStages()
                            .size());
                    Assert.assertTrue("Could not find expected stage: query variables", nextNormalisationRule
                            .getStages().contains(NormalisationRuleSchema.getRdfruleStageQueryVariables()));
                    Assert.assertTrue("Could not find expected stage: before results import", nextNormalisationRule
                            .getStages().contains(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport()));
                    Assert.assertTrue("Could not find expected stage: after results import", nextNormalisationRule
                            .getStages().contains(NormalisationRuleSchema.getRdfruleStageAfterResultsImport()));
                    
                    Assert.assertEquals(
                            "Description was not parsed correctly",
                            "Provides conversion between the deprecated Bio2RDF Plant Ontology namespace and the OAS Plant Ontology namespace using a simple prefix mapping.",
                            nextNormalisationRule.getDescription());
                    Assert.assertEquals("Order was not parsed correctly", 100, nextNormalisationRule.getOrder());
                    Assert.assertEquals("Include exclude order was not parsed correctly",
                            ProfileSchema.getProfileIncludeThenExcludeUri(),
                            nextNormalisationRule.getProfileIncludeExcludeOrder());
                    
                    Assert.assertEquals("Related namespaces were not parsed correctly", 1, nextNormalisationRule
                            .getRelatedNamespaces().size());
                    
                    Assert.assertTrue(
                            "Related namespace was not parsed correctly",
                            nextNormalisationRule.getRelatedNamespaces().contains(
                                    this.testValueFactory.createURI("http://oas.example.org/ns:po")));
                    
                    Assert.assertTrue(
                            "Normalisation rule was not implemented using the PrefixMappingNormalisationRule interface",
                            nextNormalisationRule instanceof PrefixMappingNormalisationRule);
                    
                    final PrefixMappingNormalisationRule nextPrefixMappingRule =
                            (PrefixMappingNormalisationRule)nextNormalisationRule;
                    
                    Assert.assertEquals("Input URI prefix was not parsed correctly", "http://bio2rdf.org/po:",
                            nextPrefixMappingRule.getInputUriPrefix());
                    Assert.assertEquals("Output URI prefix was not parsed correctly", "http://oas.example.org/po:",
                            nextPrefixMappingRule.getOutputUriPrefix());
                    
                    Assert.assertEquals("Subject mapping predicates were not parsed correctly", 1,
                            nextPrefixMappingRule.getSubjectMappingPredicates().size());
                    Assert.assertEquals("Predicate mapping predicates were not parsed correctly", 1,
                            nextPrefixMappingRule.getPredicateMappingPredicates().size());
                    Assert.assertEquals("Object mapping predicates were not parsed correctly", 1, nextPrefixMappingRule
                            .getObjectMappingPredicates().size());
                    
                    Assert.assertTrue("Subject mapping predicates were not parsed correctly: owl:sameAs",
                            nextPrefixMappingRule.getSubjectMappingPredicates().contains(OWL.SAMEAS));
                    Assert.assertTrue("Predicate mapping predicates were not parsed correctly: owl:equivalentProperty",
                            nextPrefixMappingRule.getPredicateMappingPredicates().contains(OWL.EQUIVALENTPROPERTY));
                    Assert.assertTrue("Object mapping predicates were not parsed correctly: owl:equivalentClass",
                            nextPrefixMappingRule.getObjectMappingPredicates().contains(OWL.EQUIVALENTCLASS));
                }
                else if(nextNormalisationRuleUri.equals(this.testNormalisationRule5))
                {
                    Assert.assertEquals("Results did not contain correct normalisation rule URI",
                            this.testNormalisationRule5, nextNormalisationRule.getKey());
                    
                    Assert.assertEquals("Did not find expected number of stages", 1, nextNormalisationRule.getStages()
                            .size());
                    Assert.assertTrue(
                            "Could not find expected stage",
                            nextNormalisationRule.getStages().contains(
                                    NormalisationRuleSchema.getRdfruleStageAfterResultsImport()));
                    
                    Assert.assertEquals("Description was not parsed correctly",
                            "Tests for the existence of Neurocommons gene symbol literals",
                            nextNormalisationRule.getDescription());
                    Assert.assertEquals("Order was not parsed correctly", 110, nextNormalisationRule.getOrder());
                    Assert.assertEquals("Include exclude order was not parsed correctly",
                            ProfileSchema.getProfileExcludeThenIncludeUri(),
                            nextNormalisationRule.getProfileIncludeExcludeOrder());
                    
                    Assert.assertEquals("Related namespaces were not parsed correctly", 1, nextNormalisationRule
                            .getRelatedNamespaces().size());
                    
                    Assert.assertTrue(
                            "Related namespace was not parsed correctly",
                            nextNormalisationRule.getRelatedNamespaces().contains(
                                    this.testValueFactory.createURI("http://example.org/ns:symbol")));
                    
                    Assert.assertTrue(
                            "Normalisation rule was not implemented using the SparqlNormalisationRule interface",
                            nextNormalisationRule instanceof SparqlNormalisationRule);
                    
                    final SparqlNormalisationRule nextSparqlRule = (SparqlNormalisationRule)nextNormalisationRule;
                    
                    Assert.assertEquals("Did not parse the correct number of sparql where patterns", 1, nextSparqlRule
                            .getSparqlWherePatterns().size());
                    
                    Assert.assertEquals(
                            "Sparql construct query where pattern was not parsed correctly",
                            " ?myUri <http://purl.org/science/owl/sciencecommons/ggp_has_primary_symbol> ?primarysymbol . ",
                            nextSparqlRule.getSparqlWherePatterns().iterator().next());
                    
                    Assert.assertTrue("Normalisation rule was not implemented using the SparqlAskRule interface",
                            nextNormalisationRule instanceof SparqlAskRule);
                    
                    final SparqlAskRule nextSparqlAskRule = (SparqlAskRule)nextSparqlRule;
                    
                    Assert.assertEquals("Did not generate the correct number of ask queries", 1, nextSparqlAskRule
                            .getSparqlAskQueries().size());
                }
                else
                {
                    Assert.fail("Found a rule with a URI that we were not testing for nextNormalisationRuleUri="
                            + nextNormalisationRuleUri);
                }
                
                if(RdfUtilsTest.FAIL_ON_UNEXPECTED_TRIPLES)
                {
                    Assert.assertEquals(
                            "There were unexpected triples in the test file. This should not happen. nextNormalisationRule.class="
                                    + nextNormalisationRule.getClass().getName() + " "
                                    + nextNormalisationRule.getUnrecognisedStatements(), 0, nextNormalisationRule
                                    .getUnrecognisedStatements().size());
                }
            }
        }
        catch(final RDFParseException ex)
        {
            Assert.fail("Found unexpected RDFParseException : " + ex.getMessage());
        }
        catch(final RepositoryException ex)
        {
            Assert.fail("Found unexpected RepositoryException : " + ex.getMessage());
        }
        catch(final IOException ex)
        {
            Assert.fail("Found unexpected IOException : " + ex.getMessage());
        }
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#getProfiles(org.openrdf.repository.Repository)}.
     */
    @Test
    public void testGetProfiles()
    {
        final InputStream nextInputStream = this.getClass().getResourceAsStream("/testconfigs/profile-1.n3");
        
        try
        {
            Assert.assertNotNull("Could not find test file", nextInputStream);
            
            this.testRepositoryConnection.add(nextInputStream, "", RDFFormat.N3);
            this.testRepositoryConnection.commit();
            
            final Map<URI, Profile> results = RdfUtils.getProfiles(this.testRepository);
            
            Assert.assertEquals(2, results.size());
            
            for(final URI nextProfileUri : results.keySet())
            {
                final Profile nextProfile = results.get(nextProfileUri);
                
                Assert.assertNotNull("Profile was null", nextProfile);
                
                Assert.assertEquals("Profile key was not the same as its map URI", nextProfileUri, nextProfile.getKey());
                
                if(nextProfileUri.equals(this.testProfileUri1))
                {
                    Assert.assertEquals("Results did not contain correct profile URI", this.testProfileUri1,
                            nextProfileUri);
                    
                    Assert.assertEquals(
                            "Title was not parsed correctly",
                            "Test profile for RDF Utilities test class with all implicit not allowed and exclude by default",
                            nextProfile.getTitle());
                    Assert.assertEquals("Order was not parsed correctly", 120, nextProfile.getOrder());
                    
                    Assert.assertEquals("Default profile include exclude order was not parsed correctly",
                            ProfileSchema.getProfileIncludeThenExcludeUri(),
                            nextProfile.getDefaultProfileIncludeExcludeOrder());
                    
                    Assert.assertFalse("Allow implicit provider inclusions was not parsed correctly",
                            nextProfile.getAllowImplicitProviderInclusions());
                    Assert.assertFalse("Allow implicit query inclusions was not parsed correctly",
                            nextProfile.getAllowImplicitQueryTypeInclusions());
                    Assert.assertFalse("Allow implicit rdf rule inclusions was not parsed correctly",
                            nextProfile.getAllowImplicitRdfRuleInclusions());
                    
                    Assert.assertEquals("Did not find the expected number of included providers", 1, nextProfile
                            .getIncludeProviders().size());
                    Assert.assertEquals("Did not find the expected number of excluded providers", 1, nextProfile
                            .getExcludeProviders().size());
                    
                    Assert.assertEquals("Did not find the expected number of included query types", 1, nextProfile
                            .getIncludeQueryTypes().size());
                    Assert.assertEquals("Did not find the expected number of excluded query types", 1, nextProfile
                            .getExcludeQueryTypes().size());
                    
                    Assert.assertEquals("Did not find the expected number of included rdf rules", 1, nextProfile
                            .getIncludeRdfRules().size());
                    Assert.assertEquals("Did not find the expected number of excluded rdf rules", 1, nextProfile
                            .getExcludeRdfRules().size());
                }
                else if(nextProfileUri.equals(this.testProfileUri2))
                {
                    Assert.assertEquals("Results did not contain correct profile URI", this.testProfileUri2,
                            nextProfileUri);
                    
                    Assert.assertEquals(
                            "Title was not parsed correctly",
                            "Test profile 2 for RDF Utilities test class with all implicit allowed, and allowed by default",
                            nextProfile.getTitle());
                    Assert.assertEquals("Order was not parsed correctly", 230, nextProfile.getOrder());
                    
                    Assert.assertEquals("Default profile include exclude order was not parsed correctly",
                            ProfileSchema.getProfileExcludeThenIncludeUri(),
                            nextProfile.getDefaultProfileIncludeExcludeOrder());
                    
                    Assert.assertTrue("Allow implicit provider inclusions was not parsed correctly",
                            nextProfile.getAllowImplicitProviderInclusions());
                    Assert.assertTrue("Allow implicit query inclusions was not parsed correctly",
                            nextProfile.getAllowImplicitQueryTypeInclusions());
                    Assert.assertTrue("Allow implicit rdf rule inclusions was not parsed correctly",
                            nextProfile.getAllowImplicitRdfRuleInclusions());
                    
                    Assert.assertEquals("Did not find the expected number of included providers", 0, nextProfile
                            .getIncludeProviders().size());
                    Assert.assertEquals("Did not find the expected number of excluded providers", 0, nextProfile
                            .getExcludeProviders().size());
                    
                    Assert.assertEquals("Did not find the expected number of included query types", 0, nextProfile
                            .getIncludeQueryTypes().size());
                    Assert.assertEquals("Did not find the expected number of excluded query types", 0, nextProfile
                            .getExcludeQueryTypes().size());
                    
                    Assert.assertEquals("Did not find the expected number of included rdf rules", 0, nextProfile
                            .getIncludeRdfRules().size());
                    Assert.assertEquals("Did not find the expected number of excluded rdf rules", 0, nextProfile
                            .getExcludeRdfRules().size());
                    
                }
                else
                {
                    Assert.fail("Found a profile with a URI that we were not testing for nextProfileUri="
                            + nextProfileUri);
                }
                
                if(RdfUtilsTest.FAIL_ON_UNEXPECTED_TRIPLES)
                {
                    Assert.assertEquals("There were unexpected triples in the test file. This should not happen.", 0,
                            nextProfile.getUnrecognisedStatements().size());
                }
            }
        }
        catch(final RDFParseException ex)
        {
            Assert.fail("Found unexpected RDFParseException : " + ex.getMessage());
        }
        catch(final RepositoryException ex)
        {
            Assert.fail("Found unexpected RepositoryException : " + ex.getMessage());
        }
        catch(final IOException ex)
        {
            Assert.fail("Found unexpected IOException : " + ex.getMessage());
        }
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#getProjects(org.openrdf.repository.Repository)}.
     */
    @Test
    @Ignore
    public void testGetProjects()
    {
        // TODO: Implement me!
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#getProviders(org.openrdf.repository.Repository)}.
     */
    @Test
    public void testGetProviders()
    {
        final InputStream nextInputStream = this.getClass().getResourceAsStream("/testconfigs/provider-1.n3");
        
        try
        {
            Assert.assertNotNull("Could not find test file", nextInputStream);
            
            this.testRepositoryConnection.add(nextInputStream, "", RDFFormat.N3);
            this.testRepositoryConnection.commit();
            
            final Map<URI, Provider> results = RdfUtils.getProviders(this.testRepository);
            
            Assert.assertEquals(1, results.size());
            
            for(final URI nextProviderUri : results.keySet())
            {
                final Provider nextProvider = results.get(nextProviderUri);
                
                Assert.assertNotNull("Provider was null", nextProvider);
                
                Assert.assertEquals("Provider key was not the same as its map URI", nextProviderUri,
                        nextProvider.getKey());
                
                if(nextProviderUri.equals(this.testProviderUri1))
                {
                    Assert.assertEquals("Results did not contain correct profile URI", this.testProviderUri1,
                            nextProviderUri);
                    
                    Assert.assertEquals("Title was not parsed correctly", "Test provider 1", nextProvider.getTitle());
                    
                    Assert.assertEquals("Resolution strategy was not parsed correctly",
                            ProviderSchema.getProviderProxy(), nextProvider.getRedirectOrProxy());
                    Assert.assertEquals("Resolution method was not parsed correctly",
                            HttpProviderSchema.getProviderHttpGetUrl(), nextProvider.getEndpointMethod());
                    Assert.assertFalse("Default provider status was not parsed correctly",
                            nextProvider.getIsDefaultSource());
                    
                    Assert.assertEquals("Profile include exclude order was not parsed correctly",
                            ProfileSchema.getProfileExcludeThenIncludeUri(),
                            nextProvider.getProfileIncludeExcludeOrder());
                    
                    Assert.assertEquals("Namespaces were not parsed correctly", 1, nextProvider.getNamespaces().size());
                    Assert.assertEquals("Query Types were not parsed correctly", 1, nextProvider
                            .getIncludedInQueryTypes().size());
                    Assert.assertEquals("Normalisation rules were not parsed correctly", 1, nextProvider
                            .getNormalisationUris().size());
                    
                    Assert.assertTrue("Provider was not parsed as an Http Provider",
                            nextProvider instanceof HttpProvider);
                    
                    final HttpProvider nextHttpProvider = (HttpProvider)nextProvider;
                    
                    Assert.assertEquals("Provider Http Endpoint Urls were not parsed correctly", 2, nextHttpProvider
                            .getEndpointUrls().size());
                    
                    // Test to make sure that we didn't parse it as any of the specialised providers
                    Assert.assertFalse("Provider was parsed incorrectly as a Sparql Provider",
                            nextProvider instanceof SparqlProvider);
                    
                    Assert.assertFalse("Provider was parsed incorrectly as an Http Sparql Provider",
                            nextProvider instanceof HttpSparqlProvider);
                    
                    Assert.assertFalse("Provider was parsed incorrectly as an Rdf Provider",
                            nextProvider instanceof RdfProvider);
                }
                else
                {
                    Assert.fail("Found a provider with a URI that we were not testing for nextProviderUri="
                            + nextProviderUri);
                }
                
                if(RdfUtilsTest.FAIL_ON_UNEXPECTED_TRIPLES)
                {
                    Assert.assertEquals("There were unexpected triples in the test file. This should not happen.", 0,
                            nextProvider.getUnrecognisedStatements().size());
                }
            }
        }
        catch(final RDFParseException ex)
        {
            Assert.fail("Found unexpected RDFParseException : " + ex.getMessage());
        }
        catch(final RepositoryException ex)
        {
            Assert.fail("Found unexpected RepositoryException : " + ex.getMessage());
        }
        catch(final IOException ex)
        {
            Assert.fail("Found unexpected IOException : " + ex.getMessage());
        }
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#getQueryTypes(org.openrdf.repository.Repository)}.
     */
    @Test
    public void testGetQueryTypes()
    {
        final InputStream nextInputStream = this.getClass().getResourceAsStream("/testconfigs/querytype-1.n3");
        
        try
        {
            Assert.assertNotNull("Could not find test file", nextInputStream);
            
            this.testRepositoryConnection.add(nextInputStream, "", RDFFormat.N3);
            this.testRepositoryConnection.commit();
            
            final Map<URI, QueryType> results = RdfUtils.getQueryTypes(this.testRepository);
            
            Assert.assertEquals("RdfUtils did not create the expected number of query types.", 2, results.size());
            
            for(final URI nextQueryTypeUri : results.keySet())
            {
                final QueryType nextQueryType = results.get(nextQueryTypeUri);
                
                Assert.assertNotNull("QueryType was null", nextQueryType);
                
                Assert.assertEquals("QueryType key was not the same as its map URI", nextQueryTypeUri,
                        nextQueryType.getKey());
                
                if(nextQueryTypeUri.equals(this.testQueryTypeUri1))
                {
                    Assert.assertEquals("Results did not contain correct query type URI", this.testQueryTypeUri1,
                            nextQueryTypeUri);
                    
                    Assert.assertTrue("Query type is dummy query type was not parsed correctly",
                            nextQueryType.getIsDummyQueryType());
                    
                    Assert.assertTrue("Query type is pageable was not parsed correctly", nextQueryType.getIsPageable());
                    
                    Assert.assertEquals("Query type title was not parsed correctly", "Test 1 query type",
                            nextQueryType.getTitle());
                    
                    Assert.assertFalse("Query type handle all namespaces was not parsed correctly",
                            nextQueryType.getHandleAllNamespaces());
                    
                    Assert.assertTrue("Query type is namespace specific was not parsed correctly",
                            nextQueryType.getIsNamespaceSpecific());
                    
                    Assert.assertEquals("Query type namespace match method was not parsed correctly",
                            NamespaceMatch.ALL_MATCHED, nextQueryType.getNamespaceMatchMethod());
                    
                    Assert.assertTrue("Query type include defaults was not parsed correctly",
                            nextQueryType.getIncludeDefaults());
                    
                    Assert.assertEquals("Query type query uri template string was not parsed correctly",
                            "${defaultHostAddress}${input_1}${defaultSeparator}${input_2}",
                            nextQueryType.getQueryUriTemplateString());
                    
                    Assert.assertEquals("Query type standard uri template string was not parsed correctly",
                            "${defaultHostAddress}${input_1}${defaultSeparator}${input_2}",
                            nextQueryType.getStandardUriTemplateString());
                    
                    Assert.assertTrue("Query type in robots txt was not parsed correctly",
                            nextQueryType.getInRobotsTxt());
                    
                    Assert.assertEquals("Query type profile include exclude order was not parsed correctly",
                            ProfileSchema.getProfileExcludeThenIncludeUri(),
                            nextQueryType.getProfileIncludeExcludeOrder());
                    
                    Assert.assertEquals("Query type public identifiers size was not parsed correctly", 1, nextQueryType
                            .getPublicIdentifierTags().size());
                    
                    Assert.assertTrue("Query type public identifiers were not parsed correctly", nextQueryType
                            .getPublicIdentifierTags().contains("input_1"));
                    
                    Assert.assertEquals("Query type namespace input indexes size was not parsed correctly", 1,
                            nextQueryType.getNamespaceInputTags().size());
                    
                    Assert.assertTrue("Query type namespace input indexes were not parsed correctly", nextQueryType
                            .getNamespaceInputTags().contains("input_1"));
                    
                    Assert.assertEquals("Query type semantically linked query types were not parsed correctly", 1,
                            nextQueryType.getLinkedQueryTypes().size());
                    
                    Assert.assertTrue("Query type was not parsed into a InputQueryType",
                            nextQueryType instanceof InputQueryType);
                    
                    final InputQueryType nextInputQueryType = (InputQueryType)nextQueryType;
                    
                    Assert.assertEquals("Query type expected input parameters were not parsed correctly", 2,
                            nextInputQueryType.getExpectedInputParameters().size());
                    
                    Assert.assertTrue("Query type was not parsed into a RegexInputQueryType",
                            nextQueryType instanceof RegexInputQueryType);
                    
                    final RegexInputQueryType nextRegexQueryType = (RegexInputQueryType)nextQueryType;
                    
                    Assert.assertEquals("Query type input regex was not parsed correctly", "^([\\w-]+):(.+)",
                            nextRegexQueryType.getInputRegex());
                    
                    Assert.assertTrue("Query type was not parsed into a RdfOutputQueryType",
                            nextQueryType instanceof RdfOutputQueryType);
                    
                    final RdfOutputQueryType nextRdfXmlQueryType = (RdfOutputQueryType)nextQueryType;
                    
                    Assert.assertEquals(
                            "Query type output rdf xml string was not parsed correctly",
                            "<rdf:Description rdf:about=\"${xmlEncoded_inputUrlEncoded_privateuppercase_normalisedStandardUri}\"><ns0pred:xmlUrl xmlns:ns0pred=\"${defaultHostAddress}bio2rdf_resource:\">${xmlEncoded_inputUrlEncoded_privateuppercase_normalisedQueryUri}</ns0pred:xmlUrl></rdf:Description>",
                            nextRdfXmlQueryType.getOutputString());
                    
                    Assert.assertTrue("Query type was not parsed into a ProcessorQueryType",
                            nextQueryType instanceof ProcessorQueryType);
                    
                    final ProcessorQueryType nextProcessorQueryType = (ProcessorQueryType)nextQueryType;
                    
                    Assert.assertEquals("Query type template string was not parsed correctly",
                            "CONSTRUCT { ${normalisedStandardUri} ?p ?o . } WHERE { ${endpointSpecificUri} ?p ?o . }",
                            nextProcessorQueryType.getProcessingTemplateString());
                    
                    Assert.assertTrue("Query type was not parsed into a SparqlProcessorQueryType",
                            nextQueryType instanceof SparqlProcessorQueryType);
                    
                    final SparqlProcessorQueryType nextSparqlProcessorQueryType =
                            (SparqlProcessorQueryType)nextQueryType;
                    
                    Assert.assertEquals("Query type template string was not parsed correctly",
                            "CONSTRUCT { ${normalisedStandardUri} ?p ?o . } WHERE { ${endpointSpecificUri} ?p ?o . }",
                            nextSparqlProcessorQueryType.getSparqlTemplateString());
                    
                    Assert.assertEquals(nextProcessorQueryType.getProcessingTemplateString(),
                            nextSparqlProcessorQueryType.getSparqlTemplateString());
                }
                else if(nextQueryTypeUri.equals(this.testQueryTypeUri2))
                {
                    Assert.assertEquals("Results did not contain correct query type URI", this.testQueryTypeUri2,
                            nextQueryTypeUri);
                    
                    Assert.assertFalse("Query type is dummy query type was not parsed correctly",
                            nextQueryType.getIsDummyQueryType());
                    
                    Assert.assertFalse("Query type is pageable was not parsed correctly", nextQueryType.getIsPageable());
                    
                    Assert.assertEquals("Query type title was not parsed correctly", "Test 2 query type",
                            nextQueryType.getTitle());
                    
                    Assert.assertTrue("Query type handle all namespaces was not parsed correctly",
                            nextQueryType.getHandleAllNamespaces());
                    
                    Assert.assertTrue("Query type is namespace specific was not parsed correctly",
                            nextQueryType.getIsNamespaceSpecific());
                    
                    Assert.assertEquals("Query type namespace match method was not parsed correctly",
                            NamespaceMatch.ANY_MATCHED, nextQueryType.getNamespaceMatchMethod());
                    
                    Assert.assertFalse("Query type include defaults was not parsed correctly",
                            nextQueryType.getIncludeDefaults());
                    
                    Assert.assertEquals("Query type query uri template string was not parsed correctly",
                            "${defaultHostAddress}${input_1}${defaultSeparator}${input_2}",
                            nextQueryType.getQueryUriTemplateString());
                    
                    Assert.assertEquals("Query type standard uri template string was not parsed correctly",
                            "${defaultHostAddress}${input_1}${defaultSeparator}${input_2}",
                            nextQueryType.getStandardUriTemplateString());
                    
                    Assert.assertTrue("Query type in robots txt was not parsed correctly",
                            nextQueryType.getInRobotsTxt());
                    
                    Assert.assertEquals("Query type profile include exclude order was not parsed correctly",
                            ProfileSchema.getProfileExcludeThenIncludeUri(),
                            nextQueryType.getProfileIncludeExcludeOrder());
                    
                    Assert.assertEquals("Query type public identifiers size was not parsed correctly", 1, nextQueryType
                            .getPublicIdentifierTags().size());
                    
                    Assert.assertTrue("Query type public identifiers were not parsed correctly", nextQueryType
                            .getPublicIdentifierTags().contains("input_1"));
                    
                    Assert.assertEquals("Query type namespace input indexes size was not parsed correctly", 1,
                            nextQueryType.getNamespaceInputTags().size());
                    
                    Assert.assertTrue("Query type namespace input indexes were not parsed correctly", nextQueryType
                            .getNamespaceInputTags().contains("input_1"));
                    
                    Assert.assertEquals("Query type semantically linked query types were not parsed correctly", 1,
                            nextQueryType.getLinkedQueryTypes().size());
                    
                    Assert.assertTrue("Query type was not parsed into a InputQueryType",
                            nextQueryType instanceof InputQueryType);
                    
                    final InputQueryType nextInputQueryType = (InputQueryType)nextQueryType;
                    
                    Assert.assertEquals("Query type expected input parameters were not parsed correctly", 2,
                            nextInputQueryType.getExpectedInputParameters().size());
                    
                    Assert.assertTrue("Query type was not parsed into a RdfInputQueryType",
                            nextQueryType instanceof RdfInputQueryType);
                    
                    final RdfInputQueryType nextRdfQueryType = (RdfInputQueryType)nextQueryType;
                    
                    Assert.assertEquals(
                            "Query type input sparql select was not parsed correctly",
                            "SELECT ?input_1 ?input_2 WHERE { ?testObjects rdf:type <http://example.org/rdfinputtest:type1> . ?testObjects <http://example.org/rdfinputtest:variable1> ?input_1 . ?testObjects <http://example.org/rdfinputtest:variable2> ?input_2 . }",
                            nextRdfQueryType.getSparqlInputSelect());
                    
                    Assert.assertTrue("Query type was not parsed into a RdfOutputQueryType",
                            nextQueryType instanceof RdfOutputQueryType);
                    
                    final RdfOutputQueryType nextRdfOutputQueryType = (RdfOutputQueryType)nextQueryType;
                    
                    Assert.assertEquals("Query type output format was not parsed correctly", "text/rdf+n3",
                            nextRdfOutputQueryType.getOutputRdfFormat());
                    
                    Assert.assertEquals(
                            "Query type output rdf n3 string was not parsed correctly",
                            "<${ntriplesEncoded_inputUrlEncoded_privatelowercase_normalisedStandardUri}> a <http://purl.org/queryall/query:QueryType>",
                            nextRdfOutputQueryType.getOutputString());
                    
                    final ProcessorQueryType nextProcessorQueryType = (ProcessorQueryType)nextQueryType;
                    
                    Assert.assertEquals("Query type template string was not parsed correctly",
                            "CONSTRUCT { ${normalisedStandardUri} ?p ?o . } WHERE { ${endpointSpecificUri} ?p ?o . }",
                            nextProcessorQueryType.getProcessingTemplateString());
                    
                    Assert.assertTrue("Query type was not parsed into a SparqlProcessorQueryType",
                            nextQueryType instanceof SparqlProcessorQueryType);
                    
                    final SparqlProcessorQueryType nextSparqlProcessorQueryType =
                            (SparqlProcessorQueryType)nextQueryType;
                    
                    Assert.assertEquals("Query type template string was not parsed correctly",
                            "CONSTRUCT { ${normalisedStandardUri} ?p ?o . } WHERE { ${endpointSpecificUri} ?p ?o . }",
                            nextSparqlProcessorQueryType.getSparqlTemplateString());
                    
                    Assert.assertEquals(nextProcessorQueryType.getProcessingTemplateString(),
                            nextSparqlProcessorQueryType.getSparqlTemplateString());
                }
                else
                {
                    Assert.fail("Found a query type with a URI that we were not testing for nextQueryTypeUri="
                            + nextQueryTypeUri);
                }
                
                if(RdfUtilsTest.FAIL_ON_UNEXPECTED_TRIPLES)
                {
                    Assert.assertEquals("There were unexpected triples in the test file. This should not happen. "
                            + nextQueryType.getUnrecognisedStatements().toString(), 0, nextQueryType
                            .getUnrecognisedStatements().size());
                }
            }
        }
        catch(final RDFParseException ex)
        {
            Assert.fail("Found unexpected RDFParseException : " + ex.getMessage());
        }
        catch(final RepositoryException ex)
        {
            Assert.fail("Found unexpected RepositoryException : " + ex.getMessage());
        }
        catch(final IOException ex)
        {
            Assert.fail("Found unexpected IOException : " + ex.getMessage());
        }
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#getRuleTests(org.openrdf.repository.Repository)}.
     */
    @Test
    public void testGetRuleTests()
    {
        final InputStream nextInputStream = this.getClass().getResourceAsStream("/testconfigs/ruletest-1.n3");
        
        try
        {
            Assert.assertNotNull("Could not find test file", nextInputStream);
            
            this.testRepositoryConnection.add(nextInputStream, "", RDFFormat.N3);
            this.testRepositoryConnection.commit();
            
            final Map<URI, RuleTest> results = RdfUtils.getRuleTests(this.testRepository);
            
            Assert.assertEquals(2, results.size());
            
            for(final URI nextRuleTestUri : results.keySet())
            {
                final RuleTest nextRuleTest = results.get(nextRuleTestUri);
                
                Assert.assertNotNull("RuleTest was null", nextRuleTest);
                
                Assert.assertEquals("RuleTest key was not the same as its map URI", nextRuleTestUri,
                        nextRuleTest.getKey());
                
                if(nextRuleTestUri.equals(this.testRuleTestUri1))
                {
                    Assert.assertEquals("Results did not contain correct rule test URI", this.testRuleTestUri1,
                            nextRuleTestUri);
                    
                    Assert.assertEquals("RuleTest stages were not parsed correctly", 2, nextRuleTest.getStages().size());
                    
                    Assert.assertEquals("RuleTest rules were not parsed correctly", 1, nextRuleTest.getRuleUris()
                            .size());
                    
                    Assert.assertTrue(nextRuleTest instanceof StringRuleTest);
                    
                    final StringRuleTest nextRegexRuleTest = (StringRuleTest)nextRuleTest;
                    
                    Assert.assertEquals("RuleTest input string was not parsed correctly", "http://example.org/",
                            nextRegexRuleTest.getTestInputString());
                    
                    Assert.assertEquals("RuleTest output string was not parsed correctly", "http://otherexample.net/",
                            nextRegexRuleTest.getTestOutputString());
                }
                else if(nextRuleTestUri.equals(this.testRuleTestUri2))
                {
                    Assert.assertEquals("Results did not contain correct rule test URI", this.testRuleTestUri2,
                            nextRuleTestUri);
                    
                    Assert.assertEquals("RuleTest stages were not parsed correctly", 1, nextRuleTest.getStages().size());
                    
                    Assert.assertEquals("RuleTest rules were not parsed correctly", 1, nextRuleTest.getRuleUris()
                            .size());
                    
                    Assert.assertTrue("Sparql Rule test was not parsed into a SparqlRuleTest object",
                            nextRuleTest instanceof SparqlRuleTest);
                    
                    final SparqlRuleTest nextSparqlRuleTest = (SparqlRuleTest)nextRuleTest;
                    
                    Assert.assertTrue("Expected result was not parsed correctly",
                            nextSparqlRuleTest.getExpectedResult());
                    
                    Assert.assertEquals("Sparql Ask test query was not parsed correctly",
                            " ?bio2rdfUri <http://bio2rdf.org/bio2rdf_resource:dbxref> ?symbolUri . ",
                            nextSparqlRuleTest.getTestSparqlAsk());
                    
                    Assert.assertEquals("Test input mime type was not parsed correctly", "text/rdf+n3",
                            nextSparqlRuleTest.getTestInputMimeType());
                    
                    Assert.assertEquals(
                            "Test triple string was not parsed correctly",
                            " <http://bio2rdf.org/geneid:12334> <http://purl.org/science/owl/sciencecommons/ggp_has_primary_symbol> \"Capn2\" . ",
                            nextSparqlRuleTest.getTestInputTriples());
                }
                else
                {
                    Assert.fail("Found a rule test with a URI that we were not testing for nextRuleTestUri="
                            + nextRuleTestUri);
                }
                
                if(RdfUtilsTest.FAIL_ON_UNEXPECTED_TRIPLES)
                {
                    Assert.assertEquals("There were unexpected triples in the test file. This should not happen. "
                            + nextRuleTest.getUnrecognisedStatements(), 0, nextRuleTest.getUnrecognisedStatements()
                            .size());
                }
            }
        }
        catch(final RDFParseException ex)
        {
            Assert.fail("Found unexpected RDFParseException : " + ex.getMessage());
        }
        catch(final RepositoryException ex)
        {
            Assert.fail("Found unexpected RepositoryException : " + ex.getMessage());
        }
        catch(final IOException ex)
        {
            Assert.fail("Found unexpected IOException : " + ex.getMessage());
        }
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#getBooleanFromValue(org.openrdf.model.Value)}.
     */
    @Test
    public void testGetStringBooleanFromValue()
    {
        Assert.assertTrue(RdfUtils.getBooleanFromValue(this.trueBooleanStringLiteral));
        Assert.assertFalse(RdfUtils.getBooleanFromValue(this.falseBooleanStringLiteral));
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#getBooleanFromValue(org.openrdf.model.Value)}.
     */
    @Test
    public void testGetTypedBooleanFromValue()
    {
        Assert.assertTrue(RdfUtils.getBooleanFromValue(this.trueBooleanTypedLiteral));
        Assert.assertFalse(RdfUtils.getBooleanFromValue(this.falseBooleanTypedLiteral));
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RdfUtils#getUTF8StringValueFromSesameValue(org.openrdf.model.Value)}
     * .
     */
    @Test
    public void testGetUTF8StringValueFromSesameValue()
    {
        final String testStringResult1 = RdfUtils.getUTF8StringValueFromSesameValue(this.testStringLiteral1);
        
        Assert.assertEquals("Simple ASCII Test", testStringResult1);
        
        Assert.assertEquals(17, testStringResult1.length());
        
        final String testStringResult2 = RdfUtils.getUTF8StringValueFromSesameValue(this.testStringLiteral2);
        
        Assert.assertEquals(
                "Brisbane ] ist die Hauptstadt des Bundesstaates Queensland im Nordosten Australiens. Die Stadt liegt am Brisbane River nahe an dessen Mündung in die Korallensee. Der Ballungsraum hat 2 Mio. Einwohner. Brisbane ist sowohl katholischer als auch anglikanischer Erzbischofssitz. Ein wichtiger Wirtschaftszweig ist die Erdölindustrie. Brisbane wurde 1824 unter dem Namen Moreton Bay als Strafkolonie gegründet und wurde später nach dem damaligen Gouverneur von New South Wales, Sir Thomas Brisbane benannt. Brisbane ist bekannt für seine Hochschulen, sowie auch für die nördlich und südlich der Stadtgrenze beginnenden Ferienparadiese Sunshine Coast und Gold Coast, für die Meereslage und das allgemein gute Wetter. Im von Abkürzungen geprägten australischen Englisch wird Brisbane gelegentlich auch als Brissie bezeichnet. Darüber hinaus existieren scherzhafte Namensabwandlungen wie Bris Vegas oder Brisneyland, die allerdings nur vereinzelt verwendet werden. Brisbane, is the capital and most populous city in the Australian state of Queensland and the third most populous city in Australia. Brisbane's metropolitan area has a population of over 2 million and constitutes the core of the South East Queensland agglomeration, encompassing more than 3 million people. The Brisbane central business district stands on the original European settlement and is situated inside a bend of the Brisbane River approximately 23 kilometres from its mouth at Moreton Bay. The metropolitan area extends in all directions along the floodplain of the Brisbane River valley between the bay and the Great Dividing Range. While the metropolitan area is governed by several municipalities, a large proportion of central Brisbane is governed by the Brisbane City Council which is Australia's largest Local Government Area by population. Brisbane is named after the river on which it sits which, in turn, was named after Sir Thomas Brisbane, the Governor of New South Wales from 1821 to 1825. The first European settlement in Queensland was a penal colony at Redcliffe, 28 kilometres (17 mi) north of the Brisbane central business district, in 1824. That settlement was soon abandoned and moved to North Quay in 1825. Free settlers were permitted from 1842. Brisbane was chosen as the capital when Queensland was proclaimed a separate colony from New South Wales in 1859. The city played a central role in the Allied campaign during World War II as the South West Pacific headquarters for General Douglas MacArthur. Brisbane has hosted many large cultural and sporting events including the 1982 Commonwealth Games, World Expo '88 and the final Goodwill Games in 2001. Brisbane is the largest economy between Sydney and Singapore and in 2008 it was classified as a gamma world city+ in the World Cities Study Group’s inventory by Loughborough University. It was also rated the 16th most livable city in the world in 2009 by The Economist. Este artículo trata sobre la ciudad de Brisbane. Para saber más sobre el río, véase Río Brisbane. Plantilla:Ficha de localidad Brisbane es la tercera ciudad más grande de Australia. Es la capital del estado de Queensland, en el noreste del país y tiene aproximadamente dos millones de habitantes (estimado en el 2009). Se encuentra al este de la Gran Cordillera Divisoria, al sureste de la cordillera Taylor y muy cercana a la bahía Moretón. Es atravesada por el río Brisbane, que ha sido dragado para facilitar el tránsito de barcos. Hacia el oeste el horizonte está dominado por la presencia del monte Cootha en cuyas faldas se encuentra un planetario y los jardines botánicos. En su cima se encuentra un mirador que ofrece unas vistas magníficas de la ciudad y su ondulante río. Las más importantes estaciones de televisión en el estado de Queensland tienen sus estudios en el monte Cootha. El sector de Southbank, que se ubica en la ribera sur del río Brisbane, se renovó intensamente para acoger la Exposición Internacional de 1988, pasando a convertirse de sus orígenes industriales a un agradable parque de hermosos jardines, donde también se encuentra una playa pública artificial, restaurantes y bares, espacios de esparcimiento y el museo marítimo. Con la construcción del puente peatonal Good Will se puede llegar fácilmente desde el centro de la ciudad a Southbank en tan sólo unos minutos. El área es una de las favoritas de los habitantes de Brisbane para celebrar asados familiares los fines de semana. El municipio de Brisbane ha anunciado un plan de desarrollo que considera la construcción de puentes peatonales adicionales con el fin de hacer la ciudad más accesible a peatones y ciclistas, así como también para incentivar las actividades físicas. La economía se basa en diversas industrias petroquímicas, metalúrgicas, construcciones mecánicas, alimentarias y ferrometalúrgicas. Destaca también su puerto, por el cual exporta carbón y metales. Es también un importante centro cultural y turístico, además de ser centro de nudo ferroviario y aéreo para Queensland. En sus proximidades se encuentra la terminal de un gasoducto y la del oleoducto de Moonie. Brisbane on Queenslandin osavaltion pääkaupunki ja sen suurin kaupunki, mutta myös koko Australian kolmanneksi suurin kaupunki vajaalla kahdella miljoonalla asukkaallaan. Kaupunki sijaitsee Tyynenmeren läheisyydessä Brisbanejoen rannalla Moreton Bayn ja Australian Kordillieerien välisellä rannikkotasangolla Queenslandin kaakkoisosassa. Brisbane è la città più popolosa nello stato australiano del Queensland e la terza città più popolosa dell'Australia. L'area metropolitana di Brisbane ha una popolazione di circa 2 milioni di abitanti. Un residente di Brisbane è comunemente chiamato \"Brisbanite\". Il distretto centrale degli affari di Brisbane è situato sull'insediamento originale, posto all'interno di un'ansa del fiume Brisbane, approssimativamente a 23 km dalla sua foce a Moreton Bay. L'area metropolitana si estende in tutte le direzioni lungo la piana alluvionale della valle del fiume Brisbane, tra la baia e la Gran Catena Divisoria. Poiché la città è governata da numerose municipalità, queste sono accentrate attorno al Concilio della Città di Brisbane che ha giurisdizione sulla più vasta area e popolazione nella Brisbane metropolitana ed è anche l'Area di Governo australiano locale più grande per popolazione. Brisbane prende il nome dal fiume sul quale è sita che, a sua volta, fu chiamato così da Sir Thomas Brisbane, il Governatore del Nuovo Galles del Sud dal 1821 al 1825. Il primo insediamento europeo nel Queensland fu una colonia penale a Redcliffe, 28 km a nord del distretto degli affari di Brisbane, nel 1824. Quell'insediamento fu presto abbandonato e trasferito a North Quay nel 1825. Brisbane fu scelta come capitale quando il Queensland venne proclamato colonia separata dal Nuovo Galles del Sud nel 1859. La città ha giocato un ruolo chiave durante la Seconda Guerra Mondiale, in quanto quartier generale del Pacifico del Sud-Ovest del Generale Douglas MacArthur. Brisbane ha ospitato molti grandi eventi culturali e sportivi fra cui i Giochi del Commonwealth nel 1982, l'Esposizione universale nel 1988 e la finale dei Goodwill Games nel 2001. Nel 2008, Brisbane è stata classificata come \"gamma world city+\" nell'inventario del World Cities Study Group dall'università di Loughborough. È stata anche dichiarata 16ima città più abitabile nel mondo nel 2009 da \"The Economist\". ブリスベン（Brisbane）はオーストラリア連邦クイーンズランド州南東部（サウス・イースト・クイーンズランド地域）に位置する州都。 シドニー、メルボルンに次ぐオーストラリア第三の都市であり、オセアニア有数の世界都市。現地での発音はTemplate:IPA（ブリズベン）であるが、ここでは日本の外務省の表記にならった。 Brisbane is een stad in het oosten van Australië. Het is de hoofdstad van de deelstaat Queensland. De stad is gelegen aan de oostkust van Australië aan de Brisbane River, zo'n 20 kilometer verwijderd van Moreton Bay. Brisbane heeft 1.676.389 inwoners (2006) en is daarmee de derde stad van Australië, na Sydney en Melbourne. Brisbane is vooral bij jonge toeristen populair vanwege de twee grote uitgaansgebieden in de stad. In deze gebieden zijn veel clubs, bars, eet- en uitgaansgelegenheden. De stad heeft een subtropisch klimaat met warme zomers en zeer milde winters. Brisbane ging in 1824 als strafkolonie van start en is vernoemd naar Sir Thomas Brisbane, toenmalig gouverneur van de deelstaat Nieuw-Zuid-Wales. Toen Queensland in 1859 tot aparte deelstaat werd uitgeroepen werd Brisbane tot hoofdstad gekozen. Tot de Tweede Wereldoorlog ontwikkelde de stad zich langzaam. Tijdens WOII was het geallieerde hoofdkwartier van Generaal Douglas MacArthur in Brisbane gevestigd en speelde de stad een centrale rol in de strijd in het zuidwestelijk deel van de Stille Oceaan. In 1982 organiseerde Brisbane de Gemenebestspelen en in 1988 vond de Wereldtentoonstelling er plaats. Vanaf die periode heeft de stad een snelle groei doorgemaakt en is zij uitgegroeid tot de huidige metropool. Brisbane [/Mal:IPA/] er hovedstaden i den australske delstaten Queensland. Byen har ca. 1,8 millioner innbyggere, er Australias raskest voksende by og den tredje største byen i Australia etter Sydney og Melbourne. Gjennom byen renner Brisbane River. Brisbane [ˈbɹɪzbən ˈbɹɪzbən] – miasto w Australii, stolica stanu Queensland, położone u ujścia rzeki Brisbane do zatoki Moreton. Klimat subtropikalny z gorącymi, wilgotnymi latami i ciepłymi, łagodnymi zimami. Ważny ośrodek handlowy, naukowy (3 uniwersytety) i kulturalny (muzea, galeria sztuki); liczne parki (herbarium). Brisbane jest ośrodkiem przemysłu rafineryjnego, gumowego, stoczniowego i maszynowego. Odbywało się tu wiele znaczących wydarzeń kulturalnych i sportowych, m. in. Igrzyska Wspólnoty Narodów (Commonwealth Games) w 1982 r. , Wystawa Światowa (World Expo) w 1988 r. oraz Igrzyska Dobrej Woli w 2001 r. W mieście znajduje się polski konsulat honorowy. Brisbane é a capital do estado de Queensland e terceira maior cidade da Austrália. Brisbane [uttal:ˈbɹɪzbən uttal:ˈbɹɪzbən] är en stad i Australien med 1,9 miljoner invånare. Den är huvudstad i delstaten Queensland samt den tredje största staden i Australien och största stad i Queensland. Brisbane är byggd utefter Brisbane River, som slingrar sig igenom staden och korsas av flera broar. Namnet har staden fått efter Sir Thomas Brisbane, som var guvernör i New South Wales åren 1821-1825. 布里斯班（Template:Lang-en），是澳大利亞昆士蘭州府城，位於澳大利亞本土的東北部，北緣陽光海岸， 南鄰國際觀光勝地黃金海岸市。大都會區人口（包括週圍的衛星城市）200萬餘，是澳大利亞人口第三大都會，僅次於雪梨與墨爾本。 布里斯本靠近太平洋，東面濱臨塔斯曼海，是一個從海岸線、河川和港口往西部內陸發展的都市。其市中心位於布里斯本河畔（Brisbane River），該州即以此為政治和交通主軸再向南北伸展開發。布里斯本國內外機場和國際海港座落於布里斯本河口兩旁。 布里斯本是1982年英聯邦運動會、1988年世界博覽會，以及2001年世界友誼運動會（Goodwill Games）的主辦城市。 Бри́сбен — крупный город на восточном побережье Австралии. Административный центр штата Квинсленд. Население — 1,8 млн человек, это третий по численности город страны. Расположен в излучине Реки Брисбен приблизительно 23 км от ее устья. Brisbane est la capitale et la ville la plus peuplée de l'État du Queensland, en Australie. Située à environ 950 kilomètres au nord de Sydney, sur le fleuve Brisbane, elle s'étend sur une plaine humide bordée de collines, limitée par Moreton Bay et par les premiers contreforts de la cordillère australienne. À quelques kilomètres du centre-ville, le mont Coot-Tha accueille une plate-forme panoramique, un planetarium et des jardins botaniques. Le site de Brisbane est appelé « Mian-Jin » par les aborigènes Turrbal, ce qui signifie « L'endroit pointu ». Troisième ville d'Australie en termes de population, elle compterait 2 millions d'habitants en tenant compte de sa périphérie (Grand Brisbane). La ville doit son nom à sir Thomas Brisbane, le gouverneur de Nouvelle-Galles du Sud de 1821 à 1825. Fondée en 1824 à Redcliffe, à 28 kilomètres du centre-ville actuel, la colonie pénale de Brisbane fut ensuite déplacée en amont, dans une boucle du fleuve Brisbane. Les premiers colons libres purent s'installer à Brisbane en 1842, peu après la fermeture du centre pénitentiaire. En 1859, la scission du nord de la Nouvelle-Galles du Sud donna lieu à la création du Queensland, dont Brisbane devint la capitale. La ville joua un grand rôle pour les forces alliées pendant la Seconde Guerre mondiale et servit de quartier général au général Douglas MacArthur qui commandait les forces alliées du sud-ouest du Pacifique. Plus récemment Brisbane a accueilli les jeux du Commonwealth de 1982, l'exposition universelle Expo '88, et les Goodwill Games de 2001. L'économie de Brisbane est basée sur les industries pétrochimiques, métallurgiques, agroalimentaires et mécaniques. Son port permet l'exportation des ressources naturelles de l'État (charbon, argent, plomb, zinc). La ville est également un important centre culturel et touristique, à peu de distance des stations balnéaires de la Sunshine Coast et de la Gold Coast.",
                testStringResult2);
        
        // Add length test to (hopefully) verify that this document has not been corrupted by a
        // non-UTF-8 editor
        Assert.assertEquals(12723, testStringResult2.length());
    }
    
    /**
     * Test method for {@link org.queryall.utils.RdfUtils#getWriterFormat(java.lang.String)}.
     */
    @Test
    public void testGetWriterFormat()
    {
        Assert.assertEquals("Could not find RDF XML writer format", RDFFormat.RDFXML,
                RdfUtils.getWriterFormat("application/rdf+xml"));
        Assert.assertEquals("Could not find N3 writer format", RDFFormat.N3, RdfUtils.getWriterFormat("text/rdf+n3"));
        Assert.assertNull("Did not properly respond with null for HTML format", RdfUtils.getWriterFormat("text/html"));
    }
    
}
