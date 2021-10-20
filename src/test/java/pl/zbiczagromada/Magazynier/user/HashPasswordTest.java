package pl.zbiczagromada.Magazynier.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static org.junit.jupiter.api.Assertions.*;

class HashPasswordTest {
    private final List<TestVector> vectors = List.of(
            new TestVector("test123", HashPassword.HashAlgo.CLEARTEXT, "test123"),
            new TestVector("7*/.r_?5TBYxAV6ShEf\\XXr=yc!YstuV}R3)+5jS^HQ'\\x~Xsk%7UhA/SEqA+U4w", HashPassword.HashAlgo.CLEARTEXT, "7*/.r_?5TBYxAV6ShEf\\XXr=yc!YstuV}R3)+5jS^HQ'\\x~Xsk%7UhA/SEqA+U4w"),
            new TestVector("UHQxD2R-QVHq]Lu'8y=Q;U4X;VZ6U;,*B;VWYWc_[P7TScEz.}jK@$;=6@BGq)(M@'^\"'%uJqL3]s.@4=wy]cj,KnR.v}y_L!>Q%-CW$.TTzm6P(7va;(aP&=>M;c<A.JSg$Dq&aGvYE_BpEd'9v\\`y(]SDsVkC=)!P7T5>ZLSfarW#a^7s/GGyAq76>W6_srvN]ZR~3,?=J4rZ(m3(GWc7G>kv'ehSn/=R@\"Y5Z3nXL&rnthUj=!]F*f!hV4PRn%&V?C'Js)pAPG_G!p[>bDBWmyL^8SU;X9H6;S\\_bKc%dUSx]f->m+drAXYFgs-<EL_<:gH*%~VV>\\aEkPRv/%]N(CcyPE&gW2EWGhZ=As6ug^rW69mpnmj2Yb\\EJt5CU@)d=F8kraJe8b6y=rRv=94KFw`_WU2GpDEQ}8?_+zVfT$$TU23Px.+y9(~^-xk>v6FUe,9uSN2S)G7`S>_em'^-WDFUADp{xYak`}Stq-b-XAp*'7'/bz~2G{cvhH5bTe:#y?m5rb,pL\"MB#P#:Ke&\\=%y9*UVkBC+(6NJv7=G9!,8+n8c/u}N}4..H}T~cy'g_y7-B~PvyF!~tmw+Y!(G(vUndZsW']r[se+XXN7A3r}?JB?qb`y/6~Zm?/r*Zv9m/\"trnLm#kw2&LZ:dzWVP[XF+g!w8GjL9KNDTKynD;~Rh\\b,/n!gfPK*/*&~eN[`cT};8D`f-A]aT$#_nE]f..9Cx_#/ANTb?'Nm/_#J*JB8DrjTCka,8!e\"hY!@6~K.7x4p#Rhj\"@naB+g3[,J6g\\RH6V%zMTP\\,tFK7D'Eu_nXw?Nv9~EDc]T6#%{\"EuKMsT>7:X~+_#mW\"Yyy~?]u3u-JJg;u}782=ZFWHFr2r-jQ6/5>q=mUJ9dZWQ&YfvQ\\`$3$N%3#8w6)~/)Mx=AHfh9x__uZyD<_[8H*&,d]7Y8C=2y>\"P5w(4;3bd7p=?3m}tNMJ?~mp*YAZ*de!FHW_Wh/D<X?},w;=>W7k)x&AHbZuM5HBk*R=~h^U\\r]GK;E5v#5}%*{!)gB<SXz?nYDd9tk&Zt7mKVj-.,bcbJ_h]6zN}PQ!vU:c*.k:t]5c\"j@36P?-$(~{WR=%x[uv*w@j&Na*up,}L\"jPrXW(Uj[+qj}A=zkN/,}$ST`D{?;eY]g?)@gxA+qFaV!]%M_S\\4a95!@8mX)8<(jhU(E+'.S$JB8Sp[S@5PwjrqMvLh\\WGd`HF/,>bm)f9Eb})%:kt\"MG~\"pqn(e7/aLwJprrAQtQ}ES-z^WhQ@_2q~SseH/[.pU93<\\,.G`A#]KMA+[3Z}[w/s+Q3A'Xzx#:uk&MpGS[qTD<mtd?!*$%#B4a@z@MrjudvJ)emgE^?vV8v*Z(&nT?vP<aQ:C6\\S,+t}.}3wGy<XtHme/j$:(dQ=WS;r5w~rEVqAHZcDwc&g`#mTxkZf3(;+d~s`X[B}uL<,PzQhT[n(ZMR;ag<u3.v6MLt.{N6}dp\\Npw=qd'b3/BH)LZ5uZ+HPJtw)\"m!-\\8X!Pyeq*2qV,7<*!eYrEX!N:HtT\"[H4m%~bS$%>&5}\">c-JLC)fmyqs:`e}^h?K)rZ<Zj?*JcU#J[f,h~a7\\#C>rU2tKkW3BK`{N\"\"fMG?DV@JLc}&(b.}<s*#Jj(Q~#)VxE,hwmbJE8mU]Mtf2@f~EmDZ;5~*:R`{C[gqqq@&gGfn~8amb8L8dB?CDa3EM$L>=q7;mZX\"V`\\YTT)y_PKHE/Dn'P!W3M5Yq-]gEPN~C+\\b`e}kCDQu@);JR3DEa#jHVF{[TF<x%fj<w+nxFv3UJ*[,6YPsn{/`H=n8#)N^&aF\"E,pB6&`HgCH+a)n)qFC},Rw<ZB$s*kJHndgB7D5uy'\\aVs!Rgfa6Y.%/X?+B(^+/L=gn]E5;7Ds/u/N5Ya_T\\r;]FZB3649WvR-~Yg`v]X#=e\"`p8p\"5t_!2h@tMkp\\Bw.ZZ,R;mK_[/E!(/dj@/&`e{;u7JZx$wNdm^MRsf7/^=nMExB%6[T~gCcheppbvN@hka+(\"3CEAkF/hH]qGbr)\"-(pR~VRY;<CBQxt!B7^Pk9Lb^2", HashPassword.HashAlgo.CLEARTEXT, "UHQxD2R-QVHq]Lu'8y=Q;U4X;VZ6U;,*B;VWYWc_[P7TScEz.}jK@$;=6@BGq)(M@'^\"'%uJqL3]s.@4=wy]cj,KnR.v}y_L!>Q%-CW$.TTzm6P(7va;(aP&=>M;c<A.JSg$Dq&aGvYE_BpEd'9v\\`y(]SDsVkC=)!P7T5>ZLSfarW#a^7s/GGyAq76>W6_srvN]ZR~3,?=J4rZ(m3(GWc7G>kv'ehSn/=R@\"Y5Z3nXL&rnthUj=!]F*f!hV4PRn%&V?C'Js)pAPG_G!p[>bDBWmyL^8SU;X9H6;S\\_bKc%dUSx]f->m+drAXYFgs-<EL_<:gH*%~VV>\\aEkPRv/%]N(CcyPE&gW2EWGhZ=As6ug^rW69mpnmj2Yb\\EJt5CU@)d=F8kraJe8b6y=rRv=94KFw`_WU2GpDEQ}8?_+zVfT$$TU23Px.+y9(~^-xk>v6FUe,9uSN2S)G7`S>_em'^-WDFUADp{xYak`}Stq-b-XAp*'7'/bz~2G{cvhH5bTe:#y?m5rb,pL\"MB#P#:Ke&\\=%y9*UVkBC+(6NJv7=G9!,8+n8c/u}N}4..H}T~cy'g_y7-B~PvyF!~tmw+Y!(G(vUndZsW']r[se+XXN7A3r}?JB?qb`y/6~Zm?/r*Zv9m/\"trnLm#kw2&LZ:dzWVP[XF+g!w8GjL9KNDTKynD;~Rh\\b,/n!gfPK*/*&~eN[`cT};8D`f-A]aT$#_nE]f..9Cx_#/ANTb?'Nm/_#J*JB8DrjTCka,8!e\"hY!@6~K.7x4p#Rhj\"@naB+g3[,J6g\\RH6V%zMTP\\,tFK7D'Eu_nXw?Nv9~EDc]T6#%{\"EuKMsT>7:X~+_#mW\"Yyy~?]u3u-JJg;u}782=ZFWHFr2r-jQ6/5>q=mUJ9dZWQ&YfvQ\\`$3$N%3#8w6)~/)Mx=AHfh9x__uZyD<_[8H*&,d]7Y8C=2y>\"P5w(4;3bd7p=?3m}tNMJ?~mp*YAZ*de!FHW_Wh/D<X?},w;=>W7k)x&AHbZuM5HBk*R=~h^U\\r]GK;E5v#5}%*{!)gB<SXz?nYDd9tk&Zt7mKVj-.,bcbJ_h]6zN}PQ!vU:c*.k:t]5c\"j@36P?-$(~{WR=%x[uv*w@j&Na*up,}L\"jPrXW(Uj[+qj}A=zkN/,}$ST`D{?;eY]g?)@gxA+qFaV!]%M_S\\4a95!@8mX)8<(jhU(E+'.S$JB8Sp[S@5PwjrqMvLh\\WGd`HF/,>bm)f9Eb})%:kt\"MG~\"pqn(e7/aLwJprrAQtQ}ES-z^WhQ@_2q~SseH/[.pU93<\\,.G`A#]KMA+[3Z}[w/s+Q3A'Xzx#:uk&MpGS[qTD<mtd?!*$%#B4a@z@MrjudvJ)emgE^?vV8v*Z(&nT?vP<aQ:C6\\S,+t}.}3wGy<XtHme/j$:(dQ=WS;r5w~rEVqAHZcDwc&g`#mTxkZf3(;+d~s`X[B}uL<,PzQhT[n(ZMR;ag<u3.v6MLt.{N6}dp\\Npw=qd'b3/BH)LZ5uZ+HPJtw)\"m!-\\8X!Pyeq*2qV,7<*!eYrEX!N:HtT\"[H4m%~bS$%>&5}\">c-JLC)fmyqs:`e}^h?K)rZ<Zj?*JcU#J[f,h~a7\\#C>rU2tKkW3BK`{N\"\"fMG?DV@JLc}&(b.}<s*#Jj(Q~#)VxE,hwmbJE8mU]Mtf2@f~EmDZ;5~*:R`{C[gqqq@&gGfn~8amb8L8dB?CDa3EM$L>=q7;mZX\"V`\\YTT)y_PKHE/Dn'P!W3M5Yq-]gEPN~C+\\b`e}kCDQu@);JR3DEa#jHVF{[TF<x%fj<w+nxFv3UJ*[,6YPsn{/`H=n8#)N^&aF\"E,pB6&`HgCH+a)n)qFC},Rw<ZB$s*kJHndgB7D5uy'\\aVs!Rgfa6Y.%/X?+B(^+/L=gn]E5;7Ds/u/N5Ya_T\\r;]FZB3649WvR-~Yg`v]X#=e\"`p8p\"5t_!2h@tMkp\\Bw.ZZ,R;mK_[/E!(/dj@/&`e{;u7JZx$wNdm^MRsf7/^=nMExB%6[T~gCcheppbvN@hka+(\"3CEAkF/hH]qGbr)\"-(pR~VRY;<CBQxt!B7^Pk9Lb^2")
    );

    @Test
    void validate() {
        ListIterator<TestVector> iterator = vectors.listIterator();
        while(iterator.hasNext()){
            TestVector vector = iterator.next();

            HashPassword hashPassword = new HashPassword(vector.cleartext, vector.algo);

            assertEquals(vector.encrypted, hashPassword.getPassword());
        }
    }

    @RequiredArgsConstructor
    private class TestVector {
        public final String cleartext;
        public final HashPassword.HashAlgo algo;
        public final String encrypted;
    }
}